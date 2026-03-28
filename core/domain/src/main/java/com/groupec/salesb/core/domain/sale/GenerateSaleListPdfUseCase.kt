package com.groupec.salesb.core.domain.sale

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getBitmapFromVectorDrawable
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.toDate
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class GenerateSaleListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        sales: List<Sale>,
        searchQuery: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        devise: String? = null,
        isServiceView: Boolean = false
    ) = generateSaleListPdf(context, sales, searchQuery, startDate, endDate, devise, isServiceView)
}

fun generateSaleListPdf(
    context: Context,
    sales: List<Sale>,
    searchQuery: String?,
    startDate: String?,
    endDate: String?,
    devise: String?,
    isServiceView: Boolean = false
): ByteArray {
    val baos = ByteArrayOutputStream()
    val writer = PdfWriter(baos)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
    val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

    getDrawableResIdIfExists(context)?.let {
        val bitmap = getBitmapFromVectorDrawable(context, it)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageData = ImageDataFactory.create(stream.toByteArray())
        val image = Image(imageData)
        image.scaleToFit(150f, 60f)
        document.add(image)
    }

    document.add(Paragraph("").setMarginBottom(8f))
    document.add(
        Paragraph(context.getString(R.string.sale_list_title))
            .setFont(boldFont)
            .setFontSize(16f)
    )
    document.add(
        Paragraph()
            .add(Text(context.getString(R.string.export_date)).setFont(boldFont))
            .add(Text(" ${currentLocalDateString()}").setFont(normalFont))
    )

    val filters = buildSaleFilterLines(context, searchQuery, startDate, endDate)
    filters.forEach { line ->
        document.add(
            Paragraph()
                .add(Text(context.getString(R.string.filter_data)).setFont(boldFont))
                .add(Text(" $line").setFont(normalFont))
        )
    }

    document.add(
        Paragraph(context.getString(R.string.sale_list_total, sales.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val saleTable = Table(UnitValue.createPercentArray(floatArrayOf(2.5f, 2.5f, 5f)))
        .useAllAvailableWidth()
    // saleTable.addHeaderCell(createCell(context.getString(R.string.id_header), isHeader = true))
    saleTable.addHeaderCell(createCell(context.getString(R.string.date_time_header), isHeader = true))
    saleTable.addHeaderCell(createCell(context.getString(R.string.amount), isHeader = true))
    saleTable.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    sales.forEach { sale ->
        // saleTable.addCell(createCell(sale.id?.toString() ?: "-"))
        saleTable.addCell(createCell(sale.datevente?.convertToLocaleDateTimeFormat()?.replace(" - ", " : ") ?: "-"))
        saleTable.addCell(createCell(sale.totalprix.formatAmount().plus(" $devise")))
        saleTable.addCell(createCell(buildSaleDetails(context, sale, isServiceView), fontSize = 9f))
    }
    document.add(saleTable)

    document.close()
    return baos.toByteArray()
}

private fun buildSaleFilterLines(
    context: Context,
    searchQuery: String?,
    startDate: String?,
    endDate: String?
): List<String> {
    val filters = mutableListOf<String>()
    if (!searchQuery.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_amount, searchQuery))
    }
    if (!startDate.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_start_date, startDate.toLocaleDate()))
    }
    if (!endDate.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_end_date, endDate.toLocaleDate()))
    }
    return filters
}

private fun buildSaleDetails(context: Context, sale: Sale, isServiceView: Boolean): String {
    val none = "-"
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = isServiceView,
        plural = true,
        capitalize = true
    )
    val paymentType = sale.paymenttype
        ?.takeIf { it.isNotBlank() }
        ?.let { value ->
            paymentTypeLibelleResFromValue(value)?.let { context.getString(it) } ?: value
        } ?: none
    val username = sale.username?.takeIf { it.isNotBlank() } ?: none
    val productLines = if (sale.details.isNotEmpty()) {
        sale.details.joinToString("\n") { detail ->
            val label = detail.libelle?.takeIf { it.isNotBlank() } ?: none
            "\t${detail.qte.formatQuantityNoTrailingZero()} x $label"
        }
    } else {
        "\t$none"
    }

    return listOf(
        "${context.getString(R.string.payment_type_header)}: $paymentType",
        "${context.getString(R.string.products_header, catalogLabelPlural)}:",
        productLines
    ).joinToString("\n")
}

private fun String.toLocaleDate(): String {
    return toDate(format = "yyyy-MM-dd")?.convertToLocaleDateTimeFormat(excludeTime = true) ?: this
}

private fun Double.formatQuantityNoTrailingZero(): String {
    return if (this % 1.0 == 0.0) {
        this.toLong().toString()
    } else {
        this.toString()
            .replace(Regex("0+$"), "")
            .replace(Regex("\\.$"), "")
    }
}

private fun createCell(content: String, isHeader: Boolean = false, fontSize: Float = 10f): Cell {
    val cell = Cell().add(Paragraph(content).setFontSize(fontSize))
    cell.setPadding(5f)
    cell.setBorder(SolidBorder(0.5f))
    if (isHeader) {
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY)
        cell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
    }
    return cell
}
