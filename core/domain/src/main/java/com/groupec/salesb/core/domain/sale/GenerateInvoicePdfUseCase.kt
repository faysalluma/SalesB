package com.groupec.salesb.core.domain.sale

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getBitmapFromVectorDrawable
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.toPercentFormat
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class GenerateInvoicePdfUseCase @Inject constructor() {
    operator fun invoke(context: Context, sale: Sale, parameter: Parameter, invoicing: Invoicing) = generateInvoicePdf(
        context,
        sale,
        parameter,
        invoicing
    )
}


fun generateInvoicePdf(
    context: Context,
    sale: Sale,
    parameter: Parameter,
    invoicing: Invoicing
): ByteArray {
    val baos = ByteArrayOutputStream()
    val writer = PdfWriter(baos)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
    val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)

    val pageSize = pdf.defaultPageSize
    val availableWidth = pageSize.width - document.leftMargin - document.rightMargin
    val rightZoneCellWidth = availableWidth * 0.5f
    val rightZoneContentWidth = rightZoneCellWidth * 0.8f
    val rightZoneSpacerWidth = rightZoneCellWidth * 0.2f

    /* Header */

    // Add logo
    getDrawableResIdIfExists(context)?.let {
        val bitmap = getBitmapFromVectorDrawable(context, it)
        // Convert Bitmap en byte array PNG
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()

        val imageData = ImageDataFactory.create(imageBytes)
        val image = Image(imageData)
        image.scaleToFit(150f, 60f)
        document.add(image)
    }
    document.add(Paragraph("\n"))

    // Add entreprise details and facturation ligne
    // Table 2 colonnes largeur égale (50% chacune)
    val headerInfoBusinessClient = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        .useAllAvailableWidth()
        .setFixedLayout()


    // Left zone
    val leftZone = Cell()
    leftZone.add(Paragraph(parameter.raisonsociale).setFont(boldFont).setFontSize(16f))
    leftZone.add(Paragraph(parameter.adresse).setFont(normalFont).setFontSize(10f))
    leftZone.add(Paragraph(parameter.telephone).setFont(normalFont).setFontSize(10f))
    parameter.email?.let {
        leftZone.add(Paragraph(it).setFont(normalFont).setFontSize(10f))
    }
    leftZone.setTextAlignment(TextAlignment.LEFT)
    leftZone.setBorder(Border.NO_BORDER)
    leftZone.setMinWidth(275f)

    // Right zone
    val rightZone = Cell()
    val rightZoneTable = Table(
        UnitValue.createPointArray(floatArrayOf(rightZoneSpacerWidth, rightZoneContentWidth))
    )
        .setWidth(UnitValue.createPointValue(rightZoneCellWidth))
        .setHorizontalAlignment(HorizontalAlignment.RIGHT)
        .setFixedLayout()
        .setBorder(Border.NO_BORDER)
    val rightSpacerCell = Cell()
        .setBorder(Border.NO_BORDER)
    val rightContentCell = Cell()
        .setBorder(Border.NO_BORDER)
        .setTextAlignment(TextAlignment.RIGHT)
    rightContentCell.add(
        Paragraph(context.getString(com.groupec.salesb.core.R.string.billing_address))
            .setFont(boldFont)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    rightContentCell.add(
        Paragraph(invoicing.fullName.uppercase())
            .setFont(normalFont)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    rightContentCell.add(
        Paragraph(invoicing.address.uppercase())
            .setFont(normalFont)
            .setTextAlignment(TextAlignment.RIGHT)
    )
    rightZoneTable.addCell(rightSpacerCell)
    rightZoneTable.addCell(rightContentCell)
    rightZone.add(rightZoneTable)
    rightZone.setBorder(Border.NO_BORDER)
    headerInfoBusinessClient.addCell(leftZone)
    headerInfoBusinessClient.addCell(rightZone)
    document.add(headerInfoBusinessClient)

    document.add(Paragraph("\n"))

    // Invoice Info
    sale.datevente?.let {
        document.add(
            Paragraph()
                .add(Text(context.getString(com.groupec.salesb.core.R.string.date_of_checkout)).setFont(boldFont))
                .add(Text(" ${it.convertToLocaleDateTimeFormat(excludeTime = true)}").setFont(normalFont))
        )
    }
    sale.id?.let {
        document.add(
            Paragraph()
                .add(Text(context.getString(com.groupec.salesb.core.R.string.invoice_no)).setFont(boldFont))
                .add(Text(" $it").setFont(normalFont))
        )
    }
    document.add(
        Paragraph()
            .add(Text(context.getString(com.groupec.salesb.core.R.string.invoice_date)).setFont(boldFont))
            .add(Text(" ${currentLocalDateString()}").setFont(normalFont))
    )
    val paidByValue = sale.paymenttype
        ?.takeIf { it.isNotBlank() }
        ?.let { paymentType ->
            paymentTypeLibelleResFromValue(paymentType)?.let(context::getString) ?: paymentType
        }
    paidByValue?.let {
        document.add(
            Paragraph()
                .add(Text(context.getString(com.groupec.salesb.core.R.string.paid_by_no_param)).setFont(boldFont))
                .add(Text(" $it").setFont(normalFont))
        )
    }

    document.add(Paragraph("\n"))

    // Product Table
    val productTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 2f, 2f, 3f)))
        .useAllAvailableWidth()
    // productTable.addHeaderCell(createCell("ID", true))
    productTable.addHeaderCell(createCell(context.getString(R.string.label), true))
    productTable.addHeaderCell(createCell(context.getString(R.string.price), true))
    productTable.addHeaderCell(createCell(context.getString(R.string.quantity), true))
    productTable.addHeaderCell(createCell(context.getString(R.string.amount), true))

    var totalSale = 0.0
    for (detail in sale.details) {
        val montant = detail.qte * detail.prix
        totalSale += montant
        //  productTable.addCell(createCell(detail.id.toString()))
        productTable.addCell(createCell(detail.libelle ?: ""))
        productTable.addCell(createCell(detail.prix.formatAmount(forceStyleFrenchUseDot = true)))
        productTable.addCell(createCell(detail.qte.toString()))
        productTable.addCell(createCell(montant.formatAmount(forceStyleFrenchUseDot = true)))
    }
    document.add(productTable)

    document.add(Paragraph("\n"))

    // Add summary and totam
    // Table 2 colonnes largeur égale (50% chacune)
    val footerSummaryTotal= Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
        .useAllAvailableWidth()

    // Left zone
    val footerSTLeftZone = Cell()
    footerSTLeftZone.add(
        Paragraph(
            context.getString(
                com.groupec.salesb.core.R.string.invoice_summary,
                sale.details.size
            )
        )
        .setFont(boldFont)
        .setFontSize(12f)
    )
    footerSTLeftZone.setPaddingRight(18f)
    footerSTLeftZone.setTextAlignment(TextAlignment.LEFT)
    footerSTLeftZone.setBorder(Border.NO_BORDER)

    // Right zone
    val footerSTRightZone = Cell()
    footerSTRightZone.add(
        Paragraph(
            context.getString(
                com.groupec.salesb.core.R.string.infos_tva,
                parameter.tva.toPercentFormat()
            )
        )
        .setFont(boldFont)
        .setFontSize(12f)
        .setTextAlignment(TextAlignment.RIGHT)
        .setPaddingBottom(18f)
    )
    footerSTRightZone.add(
        Paragraph(
            context.getString(
                R.string.total_sales,
                totalSale.formatAmount(forceStyleFrenchUseDot = true) + " " + parameter.devise
            )
        )
        .setFont(boldFont)
        .setFontSize(12f)
        .setTextAlignment(TextAlignment.RIGHT)
    )
    footerSTRightZone.setPaddingLeft(18f)
    footerSTRightZone.setTextAlignment(TextAlignment.RIGHT)
    footerSTRightZone.setBorder(Border.NO_BORDER)

    footerSummaryTotal.addCell(footerSTLeftZone)
    footerSummaryTotal.addCell(footerSTRightZone)
    document.add(footerSummaryTotal)

    document.close()

    return baos.toByteArray()
}

private fun createCell(content: String, isHeader: Boolean = false): Cell {
    val cell = Cell().add(Paragraph(content))
    cell.setPadding(5f)
    cell.setBorder(SolidBorder(0.5f))
    if (isHeader) {
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY)
        cell.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
    }
    return cell
}
