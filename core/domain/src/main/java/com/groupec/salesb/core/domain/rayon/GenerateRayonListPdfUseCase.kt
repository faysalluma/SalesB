package com.groupec.salesb.core.domain.rayon

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.getBitmapFromUrl
import com.groupec.salesb.core.model.data.Rayon
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

class GenerateRayonListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        rayons: List<Rayon>,
        searchQuery: String? = null,
        logoUrl: String? = null
    ) = generateRayonListPdf(context, rayons, searchQuery, logoUrl)
}

fun generateRayonListPdf(
    context: Context,
    rayons: List<Rayon>,
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
        Paragraph(context.getString(R.string.rayon_list_title))
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
        Paragraph(context.getString(R.string.rayon_list_total, rayons.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val table = Table(UnitValue.createPercentArray(floatArrayOf(3f, 7f))).useAllAvailableWidth()
    table.addHeaderCell(createCell(context.getString(R.string.label), isHeader = true))
    table.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    rayons.forEach { rayon ->
        table.addCell(createCell(rayon.libelle))
        table.addCell(createCell(buildRayonDetails(context, rayon), fontSize = 9f))
    }
    document.add(table)

    document.close()
    return baos.toByteArray()
}

private fun buildRayonDetails(context: Context, rayon: Rayon): String {
    val none = "-"
    val id = rayon.id?.toString() ?: none
    val description = rayon.description?.takeIf { it.isNotBlank() } ?: none
    val createdAt = rayon.datecreation?.dayMonthYear() ?: none
    return listOf(
        context.getString(R.string.id_label, id),
        context.getString(R.string.description_label, description),
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
