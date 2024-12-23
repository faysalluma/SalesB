package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SingleClickIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var clicked by remember { mutableStateOf(false) }
    val customClick = {
        if (!clicked) {
            clicked = true
            onClick()
            coroutineScope.launch {
                delay(2000L)
                clicked = false
            }
        }
    }

    IconButton(customClick, modifier, enabled, colors, interactionSource, content)
}