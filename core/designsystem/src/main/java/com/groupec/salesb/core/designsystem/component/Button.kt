package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Primary


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
fun DefaultButton(onClick: ()->Unit, text : String) {
    MaterialTheme {
        Button(
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
        ) {
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun ButtonsPreview(){
    Column {
        NextButton(onClick = { /*TODO*/ }, text = "Next")
        DefaultButton(onClick = { /*TODO*/ }, text = "Validate")
    }

}


