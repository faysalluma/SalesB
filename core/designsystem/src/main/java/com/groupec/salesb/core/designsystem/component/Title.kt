package com.groupec.salesb.core.designsystem.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Black
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

@Composable
fun TitleMedium(title: String, color : Color = Primary){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Preview
@Composable
fun TitleHeaderPreview(){
    Column {
        TitleHeader(
            title = "Welcome SalesB!",
            detail = "Cette application vous permet de gérer vos ventes et stocks"
        )
        TitleMedium(title = "Première connexion", color = Color.Unspecified)
    }

}