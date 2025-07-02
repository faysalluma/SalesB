package com.groupec.salesb.core.print

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import java.util.Locale


class Print(
    private val context: Context
) {
    fun print(logoRes: Int ?= null, sale: Sale, parameter: Parameter) {
        if (BluetoothPrintersConnections.selectFirstPaired() != null) {
            val printer = EscPosPrinter(
                BluetoothPrintersConnections.selectFirstPaired(),
                // MyBluetoothPrintersConnections.selectFirstPaired(),
                203,
                48f,
                32
            )

            val formattedText = createFormattedText(printer = printer, logoRes, sale, parameter)
            printer.printFormattedText(formattedText)
        } else {
            // Execute le toast sur le thread principale depuis un autre thread
            // (thread de fond Dispatchers.IO dans ce cas de figure)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "The printer is not connected !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createFormattedText(
        printer: EscPosPrinter,
        logoRes: Int? = null,
        sale: Sale,
        parameter: Parameter
    ): String {

        // Convert drawable to bitmap
        val drawable = logoRes?.let {
            ContextCompat.getDrawable(context, it)
        }
        val bitmap = drawable?.let {
            val width = it.intrinsicWidth.takeIf { w -> w > 0 } ?: 100
            val height = it.intrinsicHeight.takeIf { h -> h > 0 } ?: 100
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            canvas.drawColor(Color.WHITE)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            bmp
        }

        return buildString {
            // Logo or Business name
            bitmap?.let {
                append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    it
                )+"</img>\n")
            } ?: append("[C]<font size='big'>"+parameter.raisonsociale+"</font>\n")
            append("[L]\n")

            // Business Informations
            parameter.adresse.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ it.uppercase(Locale.getDefault()) +"\n")
            }
            parameter.ifu?.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ context.getString(R.string.ifu) + " " + it.uppercase(Locale.getDefault()) +"\n")
            }
            parameter.telephone.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ context.getString(R.string.tel) + " " + it +"\n")
            }
            parameter.email?.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ context.getString(R.string.email) + " " + it +"\n")
            }

            // Ligne separator
            append("[C]--------------------------------\n")

            // Date and Ticket Nimber
            append("[C]"+ context.getString(R.string.date) + " " + sale.datevente?.convertToLocaleDateTimeFormat() + "\n")
            append("[C]"+ context.getString(R.string.noticket) + " " + sale.id + "\n")


            // Add space
            append("[L]\n")

            // Products list with header
            append("[L]<b>"+ context.getString(R.string.qte_description) +
                    "[C]"+ context.getString(R.string.price) +
                    "[R]"+ context.getString(R.string.amount) + "</b>\n"
            )
            append("[L]\n")

            sale.details.forEach { productItem ->
                append("[L]"+ productItem.qte + " " + productItem.libelle +
                        "[C]"+ productItem.prix +
                        "[R]"+ productItem.prix.times(productItem.qte) + "\n"
                )
            }

            // Ligne separator
            append("[L]\n")
            append("[C]--------------------------------\n")

            // Footer
            append("[L]"+ context.getString(R.string.nb_product) + sale.details.size+ "\n")
            append("[L]\n")
            append("[L]<b><font size='tall'>"+ context.getString(R.string.total_sale) +
                    sale.details.map { it.prix * it.qte }.reduce { acc, value -> acc + value }.formatAmount() + " " +
                    parameter.devise +
                "</font></b>\n"
            )
            // TODO("Infos TVA plus tard")
            append("[L]\n")

            parameter.website?.takeIf { it.isNotEmpty() }?.let {
                append("[C]<qrcode size='20'>" + it +"</qrcode>\n")
            }
            append("[L]\n")
            // TODO("Souhaitez-vous une facture ? Rendez-vous sur .......")
            append("[C]"+context.getString(R.string.software_message)+"\n")
            append("[C]"+context.getString(R.string.thanks_you) + "\n")
            append("[L]\n")
        }
    }
}