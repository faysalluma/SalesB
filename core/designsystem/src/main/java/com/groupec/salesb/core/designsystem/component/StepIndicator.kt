package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun SignupStepIndicator(step: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth().then(modifier),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .width(34.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (index + 1 == step || (step == 4 && index == 2)) Primary else Primary.copy(alpha = 0.25f))
            )
        }
    }
}