package com.groupec.feature.salelist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.print.PrintManager
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.BitmapPrintAdapter
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.sale.GenerateInvoicePdfUseCase
import com.groupec.salesb.core.domain.sale.GetSaleUseCase
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.print.Print
import com.groupec.salesb.core.sendEmailWithAttachment
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URLConnection
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSaleUseCase:GetSaleUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val generateInvoicePdfUseCase: GenerateInvoicePdfUseCase
) : ViewModel() {

    private val defaultDate = currentDateString(pattern = "yyyy-MM-dd")
    private val _startDate = MutableStateFlow(defaultDate)
    private val _endDate = MutableStateFlow(defaultDate)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _parameter = MutableStateFlow(Parameter())
    val parameter: StateFlow<Parameter> = _parameter.asStateFlow()

    // Bluetooth
    val bluetoothPrint = Print(context)
    private val _printUiState = MutableSharedFlow<FormUIState<Unit>>(replay = 0)
    val printUiState: SharedFlow<FormUIState<Unit>> = _printUiState.asSharedFlow()

    // Save pdf file
    val fileName = "receipt.pdf"
    private val _saveReceiptToDownloads = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val saveReceiptToDownloads: StateFlow<FormUIState<File>> = _saveReceiptToDownloads.asStateFlow()


    init {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().first()
        }
    }

    val pagedProducts: Flow<PagingData<Sale>> =
        combine(_searchQuery, _startDate, _endDate) { query, start, end ->
            mapOf(
                "totalprix" to query,
                "startDate" to start,
                "endDate" to end
            )
        }
            .flatMapLatest { searchParams ->
                getSaleUseCase(searchParams)
                    .onStart { _isSearching.value = true /* Indique qu'une recherche commence */ }
                    .onCompletion {
                        // Nb: Au first load, on ne rentre jamais dans le onCompletion à cause de la paignationData qui est infini,
                        // ici on rentre dedans à cause du cancel du flatMapLastest
                        _isSearching.value = false
                    } // Recherche terminée
            }
            .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateStartDateQuery(newQuery: String) {
        _startDate.value = newQuery
    }

    fun updateEndDateQuery(newQuery: String) {
        _endDate.value = newQuery
    }

    fun printThermalReceipt(sale: Sale, parameter: Parameter) {
        viewModelScope.launch(Dispatchers.IO) {
            _printUiState.emit(FormUIState.Loading)

            val result = bluetoothPrint.printWithResult(
                getDrawableResIdIfExists(context),
                sale = sale,
                parameter = parameter
            )
            if (result.isSuccess) {
                _printUiState.emit(FormUIState.Success(Unit))
            } else {
                _printUiState.emit(
                    FormUIState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    fun onPrint(activityContext: Context, sale: Sale, parameter: Parameter, invoicing: Invoicing) {
        viewModelScope.launch {
            val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)

            // Création de l'adapter et lancement de l'impression sur le main thread
            val printAdapter = BitmapPrintAdapter(pdfBytes)
            val printManager =
                activityContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print("MyPdfJob", printAdapter, null)
        }
    }

    fun sendByEmail(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ) {
        viewModelScope.launch {
            val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)
            val file = File(activityContext.cacheDir, fileName)
            file.outputStream().use { it.write(pdfBytes) }

            // 3. Envoyer l'email avec pièce jointe
            activityContext.sendEmailWithAttachment(
                addresses = arrayOf(invoicing.email),
                subject = activityContext.getString(
                    R.string.your_invoice_object,
                    parameter.raisonsociale
                ),
                body = activityContext.getString(R.string.your_invoice_body),
                attachment = file
            )
        }
    }

    fun savePdfToDownloads(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ) {
        viewModelScope.launch {
            _saveReceiptToDownloads.value = FormUIState.Loading
            try {
                val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)
                val file = withContext(Dispatchers.IO) {
                    savePdfToDownloads(activityContext, pdfBytes)
                }
                _saveReceiptToDownloads.value = FormUIState.Success(file)
            } catch (e: Exception) {
                _saveReceiptToDownloads.value = FormUIState.Error(
                    e.localizedMessage ?: "Error when saving receipt file to downloads folder"
                )
            }
        }
    }

    fun showDownloadNotification(context: Context, file: File) {

        val manager = context.getSystemService(NotificationManager::class.java)

        // Création du canal de notification pour Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                context.getString(R.string.donwload_completed),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, URLConnection.guessContentTypeFromName(file.name))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(context.getString(R.string.donwload_completed))
            .setContentText(file.name)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }

    private suspend fun generatePdf(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ): ByteArray {
        return withContext(Dispatchers.Default) {
            generateInvoicePdfUseCase(activityContext, sale, parameter, invoicing)
        }
    }

    private fun savePdfToDownloads(context: Context, pdfBytes: ByteArray): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 et +
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(pdfBytes)
                }
                // Notify system writing is finished
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        } else {
            // Android 9 et -
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(pdfBytes)
            }
            file
        }
    }
}
