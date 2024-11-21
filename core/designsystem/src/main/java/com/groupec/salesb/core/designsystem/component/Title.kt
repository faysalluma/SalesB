package com.groupec.salesb.core.designsystem.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun TitleHeader(title: String, detail: String ? = null, textAlign: TextAlign = TextAlign.Start, color : Color = Primary){
    Column {
        Text(
            text = title,
            textAlign = textAlign,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            modifier = Modifier.fillMaxWidth(),
        )
        detail?.let {Text(text = it, modifier = Modifier.padding(top = 8.dp))}
    }
}

@Composable
fun TitleMedium(title: String, color : Color = Primary){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color
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