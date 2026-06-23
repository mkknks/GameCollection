package com.gamecollection.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.ui.model.toLabel

@Composable
fun GameListItem(
    game: GameWithMaster,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = game.gameMaster.title,
                style = MaterialTheme.typography.titleMedium,
            )
            game.gameMaster.platform?.let { platform ->
                Text(
                    text = platform,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${game.collectionItem.ownershipStatus.toLabel()} / ${game.collectionItem.playStatus.toLabel()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            game.collectionItem.rating?.let { rating ->
                Text(
                    text = "評価: $rating / 10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
