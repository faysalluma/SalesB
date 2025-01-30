package com.groupec.salesb.core.designsystem.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun SalesBImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,

        ) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.salesb),
            contentDescription = null,
            modifier = Modifier.scale(0.8f)
            // .border(2.dp, Color.Gray, CircleShape),
            // colorFilter = ColorFilter.tint(Color.Blue)
        )
    }
}

@Composable
fun CardImage(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp), // Ombre de la carte
        shape = RoundedCornerShape(8.dp),  // Bords arrondis
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                /* model = ImageRequest.Builder(LocalContext.current)
                     .data(imageUrl)
                     .crossfade(true)
                     .build(),*/
                model = imageUri,
                placeholder = painterResource(AppIcons.PhotoLarge),
                error = painterResource(AppIcons.PhotoLarge),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(54.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                // .border(2.dp, Color.Gray, CircleShape),
                // colorFilter = ColorFilter.tint(Color.Blue)
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.add_image),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@Composable
fun ProductImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp), // Ombre de la carte
        shape = RoundedCornerShape(8.dp),  // Bords arrondis
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(AppIcons.Photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(16.dp)
            //.size(120.dp)
            // .clip(CircleShape)
            // .border(2.dp, Color.Gray, CircleShape),
            // colorFilter = ColorFilter.tint(Color.Blue)
        )
    }
}

@Preview
@Composable
fun SalesBImagePreview() {
    Column {
        // SalesBImage()
        CardImage(onClick = {})
        // ProductImage(imageUrl = "")
    }

}