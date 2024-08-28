package com.groupec.salesb.core.designsystem.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun TitleHeader(title: String, detail: String ? = null){
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        detail?.let {Text(text = it)}
    }
}

@Preview
@Composable
fun TitleHeaderPreview(){
    TitleHeader(
        title = "Welcome SalesB!",
        detail = "Cette application vous permet de gérer vos ventes et stocks"
    )
}