package com.groupec.salesb.core.designsystem.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
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
fun TitleLarge(title: String, color : Color = Color.Unspecified, modifier: Modifier = Modifier){
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TitleMedium(title: String, color : Color = Color.Unspecified, modifier: Modifier = Modifier){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = modifier
    )
}

@Composable
fun AppHeadLine(
    text: String,
    modifier: Modifier = Modifier,
    color : Color = Color.Unspecified,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Leading content (aligné à gauche)
        if (leadingContent != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                leadingContent()
            }
        }

        // Texte centré
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // Text(text = text, textAlign = TextAlign.Center)
            TitleNormal(title = text, color = color)
        }

        // Trailing content (aligné à droite)
        if (trailingContent != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                trailingContent()
            }
        }
    }
}




@Composable
fun TitleNormal(title: String, color : Color = Color.Unspecified, modifier: Modifier = Modifier){
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge, //.copy(fontWeight = FontWeight.W500),
        color = color,
        modifier = modifier
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
        TitleLarge(title = "Première connexion")
    }

}