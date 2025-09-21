package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme


@Composable
fun AppCheckboxParent(
    modifier: Modifier = Modifier,
    parentLabel: String? = null,
    children: Map<String, Approval>,
    selectedChildren: List<String> = emptyList(), // Liste des clés sélectionnées
    onSelectionChanged: (List<String>) -> Unit
) {
    val context = LocalContext.current

    // 🔑 l’état des enfants vient directement de selectedChildren
    val childCheckedStates = children.keys.map { key -> key in selectedChildren }

    // TriState pour le parent
    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TitleMedium(
                modifier = Modifier.padding(top = 4.dp),
                title = parentLabel ?: stringResource(R.string.select_all)
            )
            TriStateCheckbox(
                state = parentState,
                onClick = {
                    val newState = parentState != ToggleableState.On
                    val newSelection =
                        if (newState) children.keys.toList() else emptyList()
                    onSelectionChanged(newSelection)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            children.entries.forEach { (key, child) ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 9.dp, end = 8.dp),
                        text = child.getTitle(context)
                    )
                    Checkbox(
                        checked = key in selectedChildren, // 🔑 dépend directement de selectedChildren
                        onCheckedChange = { isChecked ->
                            val newSelection = if (isChecked) {
                                selectedChildren + key
                            } else {
                                selectedChildren - key
                            }
                            onSelectionChanged(newSelection)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AppCheckboxMinimal(
    label: String,
    child: String = "",
    selectedChildren: List<String> = emptyList(),
    onChecked: (Approval?) -> Unit
) {
    val isChecked = child in selectedChildren // 🔑 dérivé uniquement des props

    Row(Modifier.fillMaxWidth()) {
        TitleMedium(
            modifier = Modifier.padding(top = 9.dp, start = 0.dp),
            title = label
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                onChecked(if (checked) Approval.AUTHORIZE_VIEW else null)
            }
        )
    }
}


@Preview
@Composable
fun CheckboxParentPreview() {
    SalesBAppTheme {
        AppCheckboxParent(
            parentLabel = "Check Parent",
            children = mapOf(
                "H01" to Approval.AUTHORIZE_VIEW,
                "H02" to Approval.STAT_PERIODIC,
                "H03" to Approval.STAT_NON_PERIODIC,
                "H04" to Approval.STAT_CHART
            ),
            onSelectionChanged = {}
        )
    }
}