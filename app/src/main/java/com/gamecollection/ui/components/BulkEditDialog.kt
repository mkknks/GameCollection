package com.gamecollection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.Visibility
import com.gamecollection.ui.viewmodel.BulkEditForm

@Composable
fun BulkEditDialog(
    form: BulkEditForm,
    isUpdating: Boolean,
    errorMessage: String?,
    onOwnershipChange: (OwnershipStatus) -> Unit,
    onPlayStatusChange: (PlayStatus) -> Unit,
    onVisibilityChange: (Visibility) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!isUpdating) onDismiss() },
        title = { Text("一括変更") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("所持状態", style = MaterialTheme.typography.titleSmall)
                OwnershipStatusSelector(
                    selected = form.ownershipStatus,
                    onSelected = onOwnershipChange,
                )
                Text("プレイ状態", style = MaterialTheme.typography.titleSmall)
                PlayStatusSelector(
                    selected = form.playStatus,
                    onSelected = onPlayStatusChange,
                )
                Text("公開設定", style = MaterialTheme.typography.titleSmall)
                VisibilitySelector(
                    selected = form.visibility,
                    onSelected = onVisibilityChange,
                )
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isUpdating,
            ) {
                Text(if (isUpdating) "更新中..." else "適用")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating,
            ) {
                Text("キャンセル")
            }
        },
    )
}
