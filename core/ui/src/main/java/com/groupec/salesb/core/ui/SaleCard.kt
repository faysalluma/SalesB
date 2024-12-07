package com.groupec.salesb.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.toDate

@Composable
fun SaleCard(sale: Sale) {
    Card(
        shape = RoundedCornerShape(0.dp),
        onClick = {}
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = White
            ),
            headlineContent = {
                Text(
                    text = sale.products.joinToString { it.libelle },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Ajoute "..." si le texte est trop long
                )
            },
            supportingContent = {
                Text(text = stringResource(R.string.sale_number_and_date, sale.id, sale.date))
            },
            trailingContent = {
                Text(sale.totalprix.toString(), fontSize = 12.sp)
            }
        )
        HorizontalDivider()
    }
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun SaleCardPreview() {
    SalesBAppTheme {
        SaleCard(
            Sale(
                1,
                "12 Nov 2024",
                listOf(
                    Product(
                        id = 1,
                        libelle = "Pain",
                        prixttc = 2.0,
                        datemodif = "2022-10-02 15:22:00".toDate()!!,
                        userid = 1
                    ),
                    Product(
                        id = 2,
                        libelle = "Beurre",
                        prixttc = 4.0,
                        datemodif = "2022-10-02 15:22:00".toDate()!!,
                        userid = 1
                    ),

                    ),
                20.50
            )
        )
    }
}
