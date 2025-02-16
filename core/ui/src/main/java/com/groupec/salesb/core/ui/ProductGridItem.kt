package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.designsystem.component.ProductImage
import com.groupec.salesb.core.designsystem.icon.AppIcons.CheckCircle
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.toDate

@Composable
fun ProductGridItem(
    product: Product,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onclick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp), // Ombre de la carte
        // shape = RoundedCornerShape(8.dp),  // Bords arrondis
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        onClick = onclick
    ) {
        // Content of the card
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                ProductImage(url = product.image?.let { Constants.UPLOAD_URL.plus(it) })
                if (isChecked) {
                    Icon(
                        imageVector = CheckCircle,
                        contentDescription = "Selected",
                        tint = Primary
                    )
                }
            }

            Text(
                text = product.libelle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun ProductGridItemPreview() {
    ProductGridItem(
        product = Product(
            id = 1,
            libelle = "Pain vienois",
            prixttc = 100.0,
            datemodif = "2022:10:12 16:51".toDate(),
        ),
        isChecked = true,
        onclick = {}
    )
}