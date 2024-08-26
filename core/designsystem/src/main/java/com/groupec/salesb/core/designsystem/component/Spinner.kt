package com.groupec.salesb.core.designsystem.component

import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


/** ExposedDropdownMenu */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppExposedDropdownMenu(items: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = text,
            onValueChange = {},
            /* placeholder = {
                 Text(text = "Select a name")
             },*/
            readOnly = true,
            singleLine = true,
            label = { Text("Label") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // colors = ExposedDropdownMenuDefaults.textFieldColors(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        text = option
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/** EditableExposedDropdownMenu */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppEditableExposedDropdown(items: List<String>) {
    var text by remember { mutableStateOf(TextFieldValue()) }

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    val filteredOptions = items.filter {
        it.lowercase().contains(text.text.lowercase())
    }.sorted()

    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {

        Box(
            modifier = Modifier.background(Color.White)
        ) {
            TextField(
                // The `menuAnchor` modifier must be passed to the text field to handle
                // expanding/collapsing the menu on click. An editable text field has
                // the anchor type `PrimaryEditable`.
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                label = { Text("Label") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded,
                        // If the text field is editable, it is recommended to make the
                        // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                        // provides a better experience for certain accessibility services
                        // to choose a menu option without typing.
                        modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier.background(Color.White)
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        text =
                            TextFieldValue(
                                text = option,
                                selection = TextRange(option.length),
                            )
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/** Multi-Select Dropdown Menu */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMultiSelectDropdownMenu(items: List<String>) {
    var isExpanded by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<String>() }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var textFieldWidth by remember { mutableStateOf(screenWidth) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {

        TextField(
            value = selectedItems.joinToString(", "),
            onValueChange = {},
            placeholder = {
                Text(text = "Select a name")
            },
            readOnly = true, // Makes the TextField clickable
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            // colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .onGloballyPositioned { coordinates ->
                    // Convert size from pixels to dp
                    textFieldWidth = coordinates.size.width.pixelToDp().dp
                }
                .widthIn(max = textFieldWidth),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            items.forEach { item ->
                AnimatedContent(
                    targetState = selectedItems.contains(item),
                    label = "Animate the selected item"
                ) { isSelected ->
                    if (isSelected) {

                        DropdownMenuItem(
                            text = {
                                Text(text = item)
                            },
                            onClick = {
                                selectedItems.remove(item)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null
                                )
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text(text = item)
                            },
                            onClick = {
                                selectedItems.add(item)
                            },
                        )
                    }
                }
            }
        }
    }
}


fun Int.pixelToDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()


@Preview(showBackground = true)
@Composable
fun SpinnersPreview() {
    val items = listOf("Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppExposedDropdownMenu(items)
        AppEditableExposedDropdown(items)
        AppMultiSelectDropdownMenu(items)
    }
}


