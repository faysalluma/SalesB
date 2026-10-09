package com.groupec.salesb.core.data.repository.parameter

import android.content.Context
import android.net.Uri
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility.Companion.getRealPathFromURI
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toParameter
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParameterRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) :
    ParameterRepository {
    private data class PreparedUpload(
        val part: MultipartBody.Part,
        val file: File
    )

    override suspend fun saveParameters(parameter: Parameter): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.setParameterConfig(parameter)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getParameterStore(): Result<Parameter> {
        return try {
            Result.Success(dataStoreManager.parameterFlow.first())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun updateBusinessInfo(
        parameter: Parameter,
        logoUri: Uri?,
        tva: String?
    ): Result<Parameter> = withContext(Dispatchers.IO) {
        val session = dataStoreManager.userSessionFlow.first()
        val user = session.account?.takeIf { it.active }
            ?: return@withContext Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_no_account)))
        val company = session.selectedCompany
            ?: return@withContext Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
        val preparedLogo = logoUri?.let { prepareImageForUpload(context, it) }

        val result = executeApiCall(
            context = context,
            errorMessage = context.getString(R.string.error_updating_data),
            apiCall = {
                apiService.updateParameter(
                    userid = user.userId,
                    entrepriseid = company.companyId,
                    raisonsociale = parameter.raisonsociale.toPlainTextBody(),
                    businessType = (if (parameter.isServiceBusiness) "SERVICE" else "COMMERCE").toPlainTextBody(),
                    // This form updates all identity fields; an empty part clears an optional value.
                    ifu = parameter.ifu.orEmpty().toPlainTextBody(),
                    adresse = parameter.adresse.orEmpty().toPlainTextBody(),
                    telephone = parameter.telephone.orEmpty().toPlainTextBody(),
                    email = parameter.email.orEmpty().toPlainTextBody(),
                    website = parameter.website.orEmpty().toPlainTextBody(),
                    devise = parameter.devise.toPlainTextBody(),
                    tva = tva.orEmpty().toPlainTextBody(),
                    logoPart = preparedLogo?.part
                )
            }
        )

        when (result) {
            is Result.Success -> {
                val apiResult = result.data
                if (apiResult.error) {
                    Result.Error(Exception(apiResult.message ?: context.getString(R.string.error_updating_data)))
                } else {
                    apiResult.data?.toParameter()?.let { updatedParameter ->
                        val currentSession = dataStoreManager.userSessionFlow.first()
                        if (updatedParameter.userid != user.userId ||
                            updatedParameter.entrepriseid != company.companyId ||
                            currentSession.account?.userId != user.userId ||
                            currentSession.selectedCompanyId != company.companyId) {
                            return@withContext Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                        }
                        try {
                            dataStoreManager.setParameterOnlyBusinessDataConfig(updatedParameter)
                        } catch (exception: CancellationException) {
                            throw exception
                        } catch (exception: IllegalArgumentException) {
                            return@withContext Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                        } catch (exception: Exception) {
                            return@withContext Result.Error(exception)
                        }
                        preparedLogo?.file
                            ?.takeIf { it.absolutePath.startsWith(context.cacheDir.absolutePath) }
                            ?.delete()
                        Result.Success(updatedParameter)
                    } ?: Result.Error(Exception(context.getString(R.string.error_empty_response)))
                }
            }
            is Result.Error -> {
                if (result.exception is CancellationException) throw result.exception
                Result.Error(result.exception)
            }
            is Result.Loading -> Result.Loading
        }
    }

    override fun getParameters(): Flow<Parameter> = dataStoreManager.parameterFlow

    override suspend fun acceptTermsAndConditions(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.acceptTermsAndConditions()
                Result.Success(Unit)
            }
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    private fun String.toPlainTextBody(): RequestBody {
        return toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun prepareImageForUpload(context: Context, imageUri: Uri): PreparedUpload? {
        val filePath = getRealPathFromURI(context, imageUri) ?: return null
        val file = File(filePath)
        if (!file.exists()) return null
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "logoPart",
            file.name,
            requestBody
        )
        return PreparedUpload(part, file)
    }
}
