package com.groupec.salesb.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream

/**
 * Create pdf document for printing
 *
 * @param context The context of app or current screen
 * @param bitmap The bitmap of screen you want to print
 * @return Pdf document naming receipt
 */
class BitmapPrintAdapter(private val pdfData: ByteArray) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        callback?.onLayoutFinished(
            PrintDocumentInfo.Builder("receipt.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build(), true)
    }

    override fun onWrite(pages: Array<PageRange>, destination: ParcelFileDescriptor,
                         cancellationSignal: CancellationSignal, callback: WriteResultCallback) {
        try {
            val output = FileOutputStream(destination.fileDescriptor)
            output.write(pdfData)
            output.close()
            callback.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        }
    }
}