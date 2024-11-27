package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White


@Composable
fun NextButton(onClick: ()->Unit, text : String) {
    MaterialTheme {
        Button(
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            modifier = Modifier.padding(top = 14.dp),
            // Uses ButtonDefaults.ContentPadding by default
            /*  contentPadding = PaddingValues(
                  start = 12.dp,
                  top = 12.dp,
                  end = 20.dp,
                  bottom = 12.dp
              )*/
        ) {
            Text(text = text)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            // Inner content including an icon and a text label
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_arrow_forward_24),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }

}

@Composable
fun DefaultButton(onClick: ()->Unit, text : String, enabled : Boolean = true, isLoading : Boolean = false) {
    MaterialTheme {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            enabled = enabled,
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (isLoading)  {
                AppLoadingScreen(Modifier.wrapContentSize(), color = White)
            } else {
                Text(text = text)
            }
        }
    }
}

@Composable
fun UnderlinedTextButton(
    modifier : Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            modifier = modifier,
            text = text,
            color = Primary,
            style = TextStyle(
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}


@Preview
@Composable
fun ButtonsPreview(){
    Column {
        NextButton(onClick = { /*TODO*/ }, text = "Next")
        DefaultButton(onClick = { /*TODO*/ }, text = "Validate")
        UnderlinedTextButton(text = "Voir plus") {
            
        }
    }
}


