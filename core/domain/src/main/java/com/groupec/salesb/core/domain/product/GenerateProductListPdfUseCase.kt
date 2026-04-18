package com.groupec.salesb.core.domain.product

import android.content.Context
import android.graphics.Bitmap
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.getBitmapFromUrl
import com.groupec.salesb.core.model.data.Product
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

class GenerateProductListPdfUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        products: List<Product>,
        searchQuery: String? = null,
        logoUrl: String? = null,
        isServiceView: Boolean = false
    ) = generateProductListPdf(context, products, searchQuery, logoUrl, isServiceView)
}

fun generateProductListPdf(
    context: Context,
    products: List<Product>,
    searchQuery: String?,
    logoUrl: String?,
    isServiceView: Boolean = false
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

    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = isServiceView,
        plural = true
    )

    document.add(Paragraph("").setMarginBottom(8f))
    document.add(
        Paragraph(context.getString(R.string.product_list_title, catalogLabelPlural))
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
        Paragraph(context.getString(R.string.product_list_total, catalogLabelPlural, products.size))
            .setFont(boldFont)
            .setFontSize(12f)
    )

    document.add(Paragraph("\n"))

    val productTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 7f)))
        .useAllAvailableWidth()
    productTable.addHeaderCell(createCell(context.getString(R.string.label), isHeader = true))
    productTable.addHeaderCell(createCell(context.getString(R.string.details), isHeader = true))

    products.forEach { product ->
        productTable.addCell(createCell(product.libelle))
        productTable.addCell(createCell(buildDetails(context, product), fontSize = 9f))
    }
    document.add(productTable)

    document.close()
    return baos.toByteArray()
}

private fun buildDetails(context: Context, product: Product): String {
    val none = "-"
    val reference = product.reference?.takeIf { it.isNotBlank() } ?: none
    val description = product.description?.takeIf { it.isNotBlank() } ?: none
    val priceHt = product.prixht?.formatAmount() ?: none
    val priceTtc = product.prixttc.formatAmount()
    val stock = product.qtestock?.toString() ?: none
    val stockMin = product.stockmini?.toString() ?: none
    val category = product.categorielibelle?.takeIf { it.isNotBlank() } ?: none
    val rayon = product.rayonlibelle?.takeIf { it.isNotBlank() } ?: none

    return listOf(
        context.getString(R.string.reference_label, reference),
        context.getString(R.string.description_label, description),
        context.getString(R.string.price_ht_label, priceHt),
        context.getString(R.string.price_ttc_label, priceTtc),
        context.getString(R.string.stock_label, stock),
        context.getString(R.string.stock_min_label, stockMin),
        context.getString(R.string.category_label, category),
        context.getString(R.string.rayon_label, rayon),
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
