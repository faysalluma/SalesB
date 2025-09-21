package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.DarkGray
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver

@Composable
fun TitledBox(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Preview
@Composable
fun TitledBoxPreview() {
    SalesBAppTheme {
        TitledBox("My field set") {
            Text("Hello")
        }
    }
}
