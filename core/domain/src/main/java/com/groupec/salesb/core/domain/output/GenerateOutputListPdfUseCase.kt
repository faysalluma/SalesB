package com.groupec.salesb.core.domain.output

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getBitmapFromVectorDrawable
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Output
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

class GenerateOutputListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        outputs: List<Output>,
        searchQuery: String? = null
    ) = generateOutputListPdf(context, outputs, searchQuery)
}

fun generateOutputListPdf(
    context: Context,
    outputs: List<Output>,
    searchQuery: String?
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

    document.add(Paragraph("\n"))
    document.add(
        Paragraph(context.getString(R.string.output_list_title))
            .setFont(boldFont)
            .setFontSize(16f)
    )
    document.add(
        Paragraph()
            .add(Text(context.getString(R.string.export_date)).setFont(boldFont))
            .add(Text(" ${currentLocalDateString()}").setFont(normalFont))
    )
    if (!searchQuery.isNullOrBlank()) {
        document.add(
            Paragraph()
                .add(Text(context.getString(R.string.filter_data)).setFont(boldFont))
                .add(Text(" $searchQuery").setFont(normalFont))
        )
    }
    document.add(
        Paragraph(context.getString(R.string.output_list_total, outputs.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val table = Table(UnitValue.createPercentArray(floatArrayOf(4f, 6f))).useAllAvailableWidth()
    table.addHeaderCell(createCell(context.getString(R.string.description_header), isHeader = true))
    table.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    outputs.forEach { output ->
        table.addCell(createCell(output.description))
        table.addCell(createCell(buildOutputDetails(context, output), fontSize = 9f))
    }
    document.add(table)

    document.close()
    return baos.toByteArray()
}

private fun buildOutputDetails(context: Context, output: Output): String {
    val none = "-"
    val id = output.id?.toString() ?: none
    val price = output.prix.formatAmount()
    val createdAt = output.datecreation?.dayMonthYear() ?: none
    return listOf(
        context.getString(R.string.id_label, id),
        context.getString(R.string.price_label, price),
        context.getString(R.string.created_at_label, createdAt)
    ).joinToString("\n")
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
