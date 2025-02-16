package com.groupec.salesb.core.designsystem.component


import androidx.compose.foundation.layout.Box
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
import com.groupec.salesb.core.designsystem.theme.Primary
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.groupec.salesb.core.designsystem.theme.Blue

@Composable
fun TitleHeader(
    title: String,
    detail: String? = null,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = Primary
) {
    Column {
        Text(
            text = title,
            textAlign = textAlign,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            modifier = Modifier.fillMaxWidth(),
        )
        detail?.let { Text(text = it, modifier = Modifier.padding(top = 8.dp)) }
    }
}

@Composable
fun TitleLarge(title: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, color = color),
        modifier = modifier
    )
}

@Composable
fun TitleMedium(title: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TitleSmall(title: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextNormal(
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Text(
        textAlign = textAlign,
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun AppHeadLine(
    text: String,
    style: TextStyle =  MaterialTheme.typography.bodyLarge,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
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
            Text(
                text = text,
                style = style,
                color = color
            )
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
fun HtmlText(
    html: String,
    linkColor: Color = Blue, // Default link color
    textColor: Color = Color.Unspecified,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val uriHandler = LocalUriHandler.current
    val annotatedText = remember(html) {
        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val text = spanned.toString()
        buildAnnotatedString {
            append(text)
            val urlSpans =
                spanned.getSpans(0, spanned.length, android.text.style.URLSpan::class.java)
            urlSpans.forEach { urlSpan ->
                val start = spanned.getSpanStart(urlSpan)
                val end = spanned.getSpanEnd(urlSpan)
                val url = urlSpan.url
                addStyle(
                    style = SpanStyle(
                        color = linkColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    ), start = start, end = end
                )
                addLink(
                    url = LinkAnnotation.Url(
                        url = url,
                        linkInteractionListener = {
                            uriHandler.openUri(url)
                        }
                    ),
                    start = start,
                    end = end
                )
            }
        }
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}

@Preview
@Composable
fun TitleHeaderPreview() {
    Column {
        TitleHeader(
            title = "Welcome SalesB!",
            detail = "Cette application vous permet de gérer vos ventes et stocks"
        )
        TitleLarge(title = "Première connexion")
    }

}