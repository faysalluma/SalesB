package com.groupec.salesb.core.designsystem.component

import android.content.res.Resources
import androidx.compose.ui.geometry.Size
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.Silver3


/** ExposedDropdownMenu */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppExposedDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<String>,
    label: String ? = null,
    value: String = "", // Selector value
    onValueChange: ((String) -> Unit) ? = null, // For update selector value
    onItemSelected: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = value,
            onValueChange = {},
            /* placeholder = {
                 Text(text = "Select a name")
             },*/
            readOnly = true,
            singleLine = true,
            label = if (!label.isNullOrEmpty()) {
                { Text(label) }
            } else null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // colors = ExposedDropdownMenuDefaults.textFieldColors(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                // unfocusedIndicatorColor = Primary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            items.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onValueChange?.let { it(option) }
                        onItemSelected(index, option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppExposedDropdownMenuWithError(
    modifier: Modifier = Modifier,
    items: List<String>,
    label: String ? = null,
    value: String = "", // Selector value
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    onValueChange: ((String) -> Unit) ? = null, // For update selector value
    onItemSelected: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val supportingTextValue: (@Composable () -> Unit)? = when {
        isError -> supportingText ?: if (items[0] == value) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.required_field),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else null
        else -> null
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = value,
            onValueChange = {},
            /* placeholder = {
                 Text(text = "Select a name")
             },*/
            readOnly = true,
            singleLine = true,
            label = if (!label.isNullOrEmpty()) {
                { Text(label) }
            } else null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // colors = ExposedDropdownMenuDefaults.textFieldColors(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                // unfocusedIndicatorColor = Primary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            isError = isError,
            supportingText = supportingTextValue
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            items.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onValueChange?.let { it(option) }
                        onItemSelected(index, option)
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
fun AppEditableExposedDropdown(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    label: String? = null,
    isError: Boolean = false,
    value: TextFieldValue = TextFieldValue(""),
    onValueChange: ((TextFieldValue) -> Unit) ? = null,
    supportingText: @Composable (() -> Unit)? = null,
    onItemSelected: (Pair<String, String>) -> Unit,
)  {

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    val filteredOptions = items.filter {
        it.second.lowercase().contains(value.text.lowercase())
    }.sortedBy { it.second }


    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    // Custom elements
    val supportingTextValue: (@Composable () -> Unit)? = when {
        isError -> supportingText ?: if (value.text.isEmpty()) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.required_field),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else null
        else -> null
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
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
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth(),
                value = value,
                onValueChange = { newText ->
                    onValueChange?.let { it(newText) } // Mettre à jour l'etat externe
                    // Vérifier si la saisie correspond à une valeur valide
                    items.find { it.second == newText.text }?.let {
                        onItemSelected(it) // Appeler onItemSelected avec l'élément correspondant
                    }?:onItemSelected(Pair("", newText.text))

                },
                singleLine = true,
                label = label?.let { { Text(it) } },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded,
                        // If the text field is editable, it is recommended to make the
                        // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                        // provides a better experience for certain accessibility services
                        // to choose a menu option without typing.
                       // modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                    )
                },
                isError = isError,
                supportingText = supportingTextValue,
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            },
            modifier = Modifier.background(Color.White)
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onValueChange?.let {
                            it(TextFieldValue(
                                text = option.second,
                                selection = TextRange(option.second.length)
                            ))
                        }
                        setExpanded(false)
                        onItemSelected(option)
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
                // Garder la taille de Spinner fixe
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

/** AutoComplete Select Bar */
@Composable
fun AutoCompleteSelectBar(entries: List<String>) {

    var itemElement by remember { mutableStateOf("") }
    val heightTextFields by remember { mutableStateOf(55.dp) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightTextFields)
                .border(
                    width = 1.dp,
                    color = Silver2,
                    shape = RoundedCornerShape(8.dp)
                )
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            value = itemElement,
            onValueChange = {
                itemElement = it
                expanded = true
            },
            // Perform action when the TextField is clicked
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                },
            placeholder = { Text("Enter word") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "arrow",
                   tint = Silver3
                )
            }
        )

        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .width(textFieldSize.width.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 180.dp),
                ) {

                    if (itemElement.isNotEmpty()) {
                        items(
                            entries.filter {
                                it.lowercase().contains(itemElement.lowercase())
                            }.sorted()
                        ) {
                            ItemElement(title = it) { title ->
                                itemElement = title
                                expanded = false
                            }
                        }
                    } else {
                        items(
                            entries.sorted()
                        ) {
                            ItemElement(title = it) { title ->
                                itemElement = title
                                expanded = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemElement(
    title: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(vertical = 12.dp, horizontal = 15.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}

fun Int.pixelToDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()


@Preview(showBackground = true)
@Composable
fun SpinnersPreview() {
    val items = listOf("Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread")
    val pairItems = listOf("1" to "Cupcake" , "2" to "Donut", "3" to "Eclair")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AutoCompleteSelectBar(entries = items)
        AppExposedDropdownMenu(items = items) { index, item ->

        }
        AppEditableExposedDropdown(
            pairItems,
            value = TextFieldValue(""),
            onValueChange = {},
            onItemSelected = { item ->

            }
        )
        AppMultiSelectDropdownMenu(items)
    }
}


