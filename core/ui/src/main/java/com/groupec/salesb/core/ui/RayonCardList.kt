package com.groupec.salesb.core.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.groupec.salesb.core.model.data.Rayon


@Composable
fun RayonCardList(
    rayons: List<Rayon>,
    onViewDetail: (Rayon) -> Unit,
    onDelete: (Int, String) -> Unit,
    removeSelectedBgColor: Boolean
) {
    // Track selected item index
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Remove selected background color when click on Cancel from ProductListScreen
    LaunchedEffect(removeSelectedBgColor) {
        selectedIndex = null
    }

    LazyColumn {
        itemsIndexed(rayons) { index, rayon ->
            val isSelected = index == selectedIndex // Check if item is selected
            RayonCard(
                rayon = rayon,
                isSelected = isSelected,
                onViewDetail = {
                    selectedIndex = index
                    onViewDetail(rayon)
                },
                onDelete = onDelete
            )
        }
    }
}