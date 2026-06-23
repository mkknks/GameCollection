package com.gamecollection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility
import com.gamecollection.ui.model.toLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OwnershipStatusSelector(
    selected: OwnershipStatus,
    onSelected: (OwnershipStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    EnumChipSelector(
        values = OwnershipStatus.entries,
        selected = selected,
        onSelected = onSelected,
        label = { it.toLabel() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayStatusSelector(
    selected: PlayStatus,
    onSelected: (PlayStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    EnumChipSelector(
        values = PlayStatus.entries,
        selected = selected,
        onSelected = onSelected,
        label = { it.toLabel() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PurchaseConditionSelector(
    selected: PurchaseCondition,
    onSelected: (PurchaseCondition) -> Unit,
    modifier: Modifier = Modifier,
) {
    EnumChipSelector(
        values = PurchaseCondition.entries,
        selected = selected,
        onSelected = onSelected,
        label = { it.toLabel() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisibilitySelector(
    selected: Visibility,
    onSelected: (Visibility) -> Unit,
    modifier: Modifier = Modifier,
) {
    EnumChipSelector(
        values = Visibility.entries,
        selected = selected,
        onSelected = onSelected,
        label = { it.toLabel() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> EnumChipSelector(
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        values.forEach { value ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                label = { Text(label(value)) },
            )
        }
    }
}
