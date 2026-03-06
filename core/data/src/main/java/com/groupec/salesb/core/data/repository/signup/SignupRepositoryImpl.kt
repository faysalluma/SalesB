package com.groupec.salesb.core.data.repository.signup

import android.content.Context
import android.net.Uri
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility.Companion.deleteImageFromCache
import com.groupec.salesb.core.UploadUtility.Companion.getRealPathFromURI
import com.groupec.salesb.core.model.data.SignupConfiguration
import com.groupec.salesb.core.network.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignupRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService
) : SignupRepository {

    override suspend fun saveSignupConfiguration(
        configuration: SignupConfiguration,
        uriLogo: Uri?
    ): Result<Unit> {
        val logoPart = uriLogo?.let { prepareImageForUpload(context, it) }

        val result = runCatching {
            val response = apiService.addSignupConfiguration(
                fullName = configuration.fullName.toPlainTextBody(),
                email = configuration.email.toPlainTextBody(),
                password = configuration.password.toPlainTextBody(),
                companyName = configuration.companyName.toPlainTextBody(),
                companyType = configuration.companyType.toString().toPlainTextBody(),
                companyEmail = configuration.companyEmail?.takeIf { it.isNotBlank() }?.toPlainTextBody(),
                address = configuration.address?.takeIf { it.isNotBlank() }?.toPlainTextBody(),
                phone = configuration.phone?.takeIf { it.isNotBlank() }?.toPlainTextBody(),
                ifu = configuration.ifu?.takeIf { it.isNotBlank() }?.toPlainTextBody(),
                website = configuration.website?.takeIf { it.isNotBlank() }?.toPlainTextBody(),
                devise = configuration.devise.toPlainTextBody(),
                tva = configuration.tva.toString().toPlainTextBody(),
                useIntForPriceAndAmount = configuration.useIntForPriceAndAmount.toString().toPlainTextBody(),
                showImageOnProduct = configuration.showImageOnProduct.toString().toPlainTextBody(),
                activePaymentMode = configuration.activePaymentMode.toString().toPlainTextBody(),
                activePrinter = configuration.activePrinter.toString().toPlainTextBody(),
                logoPart = logoPart
            )

            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(HttpException(response))
            }
        }.getOrElse {
            Result.Error(it)
        }

        deleteImageFromCache(context, uriLogo?.lastPathSegment ?: "")
        return result
    }

    private fun String.toPlainTextBody(): RequestBody {
        return toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun prepareImageForUpload(context: Context, imageUri: Uri): MultipartBody.Part? {
        val filePath = getRealPathFromURI(context, imageUri) ?: return null
        val file = File(filePath)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "logoPart",
            file.name,
            requestBody
        )
    }
}
