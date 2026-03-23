package com.groupec.salesb.core.print

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.toPercentFormat
import java.util.Locale
import androidx.core.content.ContextCompat


class Print(
    private val context: Context
) {
    fun print(logoRes: Int? = null, sale: Sale, parameter: Parameter) {
        val result = printWithResult(logoRes, sale, parameter)
        if (result.isFailure) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    result.exceptionOrNull()?.message ?: context.getString(R.string.printer_not_connected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun validatePrinterReadiness(): Result<Unit> {
        return openPrinterConnection().fold(
            onSuccess = { connection ->
                connection.disconnect()
                Result.success(Unit)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    fun printWithResult(
        logoRes: Int? = null,
        sale: Sale,
        parameter: Parameter
    ): Result<Unit> {
        return try {
            openPrinterConnection().fold(
                onSuccess = { printerConnection ->
                    try {
                        val printer = EscPosPrinter(printerConnection, 203, 48f, 32)
                        val formattedText = createFormattedText(printer, logoRes, sale, parameter)
                        printer.printFormattedText(formattedText)
                        Result.success(Unit)
                    } finally {
                        printerConnection.disconnect()
                    }
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun openPrinterConnection(): Result<BluetoothConnection> {
        return try {
            if (!hasBluetoothPermissions()) {
                return Result.failure(
                    IllegalStateException(context.getString(R.string.bluetooth_permission_required))
                )
            }

            val bluetoothAdapter = getBluetoothAdapter()
                ?: return Result.failure(
                    IllegalStateException(context.getString(R.string.bluetooth_not_supported))
                )

            if (!bluetoothAdapter.isEnabled) {
                return Result.failure(
                    IllegalStateException(context.getString(R.string.bluetooth_disabled))
                )
            }

            val printerConnection = MyBluetoothPrintersConnections.selectFirstPaired()
            if (printerConnection != null && printerConnection.isConnected) {
                Result.success(printerConnection)
            } else {
                Result.failure(
                    IllegalStateException(context.getString(R.string.printer_not_connected))
                )
            }
        } catch (securityException: SecurityException) {
            Result.failure(
                IllegalStateException(context.getString(R.string.bluetooth_permission_required))
            )
        } catch (exception: Exception) {
            Result.failure(
                IllegalStateException(
                    exception.localizedMessage
                        ?: context.getString(R.string.printer_not_connected)
                )
            )
        }
    }

    private fun getBluetoothAdapter(): BluetoothAdapter? {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        return bluetoothManager?.adapter
    }

    private fun hasBluetoothPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
            )
        }

        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
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
            parameter.adresse?.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ it.uppercase(Locale.getDefault()) +"\n")
            }
            parameter.ifu?.takeIf { it.isNotEmpty() }?.let {
                append("[C]"+ context.getString(R.string.ifu) + " " + it.uppercase(Locale.getDefault()) +"\n")
            }
            parameter.telephone?.takeIf { it.isNotEmpty() }?.let {
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
            val paidByValue = sale.paymenttype
                ?.takeIf { it.isNotBlank() }
                ?.let { paymentType ->
                    paymentTypeLibelleResFromValue(paymentType)?.let(context::getString) ?: paymentType
                }
            paidByValue?.let {
                append("[C]"+ context.getString(com.groupec.salesb.core.R.string.payment_method_2, it) + "\n")
            }


            // Add space
            append("[L]\n")

            // Products list with header
            /*append("[L]<b>"+ context.getString(R.string.qte_description) +
                    "[C]"+ context.getString(R.string.price) +
                    "[R]"+ context.getString(R.string.amount) + "</b>\n"
            )
            append("[L]\n")

            sale.details.forEach { productItem ->
                append("[L]"+ productItem.qte + " " + productItem.libelle +
                        "[C]"+ productItem.prix +
                        "[R]"+ productItem.prix.times(productItem.qte) + "\n"
                )
            }*/
            append("[L]<b>"+ context.getString(R.string.description) + "</b>\n")
            append("[C]<b>"+ context.getString(R.string.qte_price) +
                    "[R]"+ context.getString(R.string.amount) + "</b>\n"
            )
            // Ligne separator
            append("[C]--------------------------------\n")

            sale.details.forEach { productItem ->
                append("[L]"+ productItem.libelle + "\n")
                append("[C]"+ productItem.qte + "   " + productItem.prix +
                       "[R]"+   (productItem.qte*productItem.prix).formatAmount() + "\n"
                )
            }

            // Ligne separator
            append("[C]--------------------------------\n")

            // Footer
            append("[L]"+ context.getString(R.string.nb_product) + sale.details.size+
                   "[R]"+ context.getString(R.string.tva, parameter.tva.toPercentFormat()) + "\n")
            append("[L]\n")
            append("[L]<b><font size='tall'>"+ context.getString(R.string.total_sale) + " " +
                    sale.details.map { it.prix * it.qte }.reduce { acc, value -> acc + value }.formatAmount() + " " +
                    parameter.devise +
                "</font></b>\n"
            )
            // TODO("Infos TVA plus tard")
            append("[L]\n")

            parameter.website?.takeIf { it.isNotEmpty() }?.let {
                append("[C]<qrcode size='20'>" + it +"</qrcode>\n")
                append("[L]\n")
            }

            // TODO("Souhaitez-vous une facture ? Rendez-vous sur .......")
            append("[C]"+context.getString(R.string.software_message)+"\n")
            append("[C]"+context.getString(R.string.thanks_you) + "\n")
            append("[L]\n")
        }
    }
}
