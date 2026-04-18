package com.groupec.salesb.core.domain.client

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.getBitmapFromUrl
import com.groupec.salesb.core.model.data.Client
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

class GenerateClientListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        clients: List<Client>,
        searchQuery: String? = null,
        logoUrl: String? = null
    ) = generateClientListPdf(context, clients, searchQuery, logoUrl)
}

fun generateClientListPdf(
    context: Context,
    clients: List<Client>,
    searchQuery: String?,
    logoUrl: String?
): ByteArray {
    val baos = ByteArrayOutputStream()
    val writer = PdfWriter(baos)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
    val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

    getBitmapFromUrl(logoUrl)?.let { bitmap ->
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageData = ImageDataFactory.create(stream.toByteArray())
        val image = Image(imageData)
        image.scaleToFit(150f, 60f)
        document.add(image)
    }

    document.add(Paragraph("").setMarginBottom(8f))
    document.add(
        Paragraph(context.getString(R.string.client_list_title))
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
        Paragraph(context.getString(R.string.client_list_total, clients.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val table = Table(UnitValue.createPercentArray(floatArrayOf(4f, 6f))).useAllAvailableWidth()
    table.addHeaderCell(createCell(context.getString(R.string.name_header), isHeader = true))
    table.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    clients.forEach { client ->
        table.addCell(createCell(client.nomprenom))
        table.addCell(createCell(buildClientDetails(context, client), fontSize = 9f))
    }
    document.add(table)

    document.close()
    return baos.toByteArray()
}

private fun buildClientDetails(context: Context, client: Client): String {
    val none = "-"
    val address = client.adresse?.takeIf { it.isNotBlank() } ?: none
    val phone = client.telephone?.takeIf { it.isNotBlank() } ?: none
    val createdAt = client.datecreation?.dayMonthYear() ?: none

    return listOf(
        context.getString(R.string.address_label, address),
        context.getString(R.string.phone_label, phone),
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
