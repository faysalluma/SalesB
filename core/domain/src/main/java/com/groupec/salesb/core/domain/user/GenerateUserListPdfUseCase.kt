package com.groupec.salesb.core.domain.user

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.getBitmapFromVectorDrawable
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.User
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

class GenerateUserListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        users: List<User>,
        searchQuery: String? = null
    ) = generateUserListPdf(context, users, searchQuery)
}

fun generateUserListPdf(
    context: Context,
    users: List<User>,
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
        Paragraph(context.getString(R.string.user_list_title))
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
        Paragraph(context.getString(R.string.user_list_total, users.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val table = Table(UnitValue.createPercentArray(floatArrayOf(4f, 6f))).useAllAvailableWidth()
    table.addHeaderCell(createCell(context.getString(R.string.name_header), isHeader = true))
    table.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    users.forEach { user ->
        table.addCell(createCell(user.nomprenom))
        table.addCell(createCell(buildUserDetails(context, user), fontSize = 9f))
    }
    document.add(table)

    document.close()
    return baos.toByteArray()
}

private fun buildUserDetails(context: Context, user: User): String {
    val none = "-"
    val id = user.id?.toString() ?: none
    val email = user.email
    val phone = user.tel?.takeIf { it.isNotBlank() } ?: none
    val address = user.adresse?.takeIf { it.isNotBlank() } ?: none
    val active = if (user.actif) context.getString(R.string.yes) else context.getString(R.string.no)
    val createdAt = user.datecreation?.dayMonthYear() ?: none

    return listOf(
        context.getString(R.string.id_label, id),
        context.getString(R.string.email_label, email),
        context.getString(R.string.phone_label, phone),
        context.getString(R.string.address_label, address),
        context.getString(R.string.active_label, active),
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
