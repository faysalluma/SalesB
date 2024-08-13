package com.groupec.salesb.core.designsystem.component


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun TitleHeader(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Primary
    )
}

@Preview
@Composable
fun TitleHeaderPreview(){
    TitleHeader(text = "Welcome SalesB!")
}