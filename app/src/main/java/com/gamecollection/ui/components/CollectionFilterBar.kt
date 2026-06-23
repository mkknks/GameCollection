package com.gamecollection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.ui.model.RatingFilter
import com.gamecollection.ui.model.toLabel
import com.gamecollection.ui.viewmodel.CollectionListFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CollectionFilterBar(
    filter: CollectionListFilter,
    availablePlatforms: List<String>,
    onOwnershipChange: (OwnershipStatus?) -> Unit,
    onPlayStatusChange: (PlayStatus?) -> Unit,
    onRatingChange: (RatingFilter) -> Unit,
    onPlatformChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("フィルタ", style = MaterialTheme.typography.titleSmall)

        FilterSection(title = "所持状態") {
            FilterChip(
                selected = filter.ownershipStatus == null,
                onClick = { onOwnershipChange(null) },
                label = { Text("すべて") },
            )
            OwnershipStatus.entries.forEach { status ->
                FilterChip(
                    selected = filter.ownershipStatus == status,
                    onClick = { onOwnershipChange(status) },
                    label = { Text(status.toLabel()) },
                )
            }
        }

        FilterSection(title = "プレイ状態") {
            FilterChip(
                selected = filter.playStatus == null,
                onClick = { onPlayStatusChange(null) },
                label = { Text("すべて") },
            )
            PlayStatus.entries.forEach { status ->
                FilterChip(
                    selected = filter.playStatus == status,
                    onClick = { onPlayStatusChange(status) },
                    label = { Text(status.toLabel()) },
                )
            }
        }

        FilterSection(title = "評価") {
            FilterChip(
                selected = filter.ratingFilter is RatingFilter.All,
                onClick = { onRatingChange(RatingFilter.All) },
                label = { Text("すべて") },
            )
            FilterChip(
                selected = filter.ratingFilter is RatingFilter.Unrated,
                onClick = { onRatingChange(RatingFilter.Unrated) },
                label = { Text("未評価") },
            )
            (1..10).forEach { rating ->
                FilterChip(
                    selected = filter.ratingFilter is RatingFilter.Exact &&
                        filter.ratingFilter.value == rating,
                    onClick = { onRatingChange(RatingFilter.Exact(rating)) },
                    label = { Text("$rating") },
                )
            }
        }

        if (availablePlatforms.isNotEmpty()) {
            FilterSection(title = "プラットフォーム") {
                FilterChip(
                    selected = filter.platform == null,
                    onClick = { onPlatformChange(null) },
                    label = { Text("すべて") },
                )
                availablePlatforms.forEach { platform ->
                    FilterChip(
                        selected = filter.platform == platform,
                        onClick = { onPlatformChange(platform) },
                        label = { Text(platform) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            content()
        }
    }
}
