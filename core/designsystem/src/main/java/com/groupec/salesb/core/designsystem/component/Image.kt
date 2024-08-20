package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.groupec.salesb.core.designsystem.R

@Composable
fun SalesBImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.salesb),
            contentDescription = null,
            // .border(2.dp, Color.Gray, CircleShape),
            // colorFilter = ColorFilter.tint(Color.Blue)
        )
    }

}

@Preview
@Composable
fun SalesBImagePreview(){
    SalesBImage()
}