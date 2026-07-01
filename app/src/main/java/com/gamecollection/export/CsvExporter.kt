package com.gamecollection.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.gamecollection.data.model.GameWithMaster
import com.gamecollection.ui.model.toLabel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CsvExportResult(
    val file: File,
    val uri: Uri,
    val rowCount: Int,
)

class CsvExporter(
    private val context: Context,
) {
    fun export(games: List<GameWithMaster>): CsvExportResult {
        val exportDir = File(context.cacheDir, "csv_exports").apply { mkdirs() }
        val fileName = "game_collection_${timestamp()}.csv"
        val file = File(exportDir, fileName)

        val csvBody = buildString {
            appendLine(CSV_HEADERS.joinToString(",") { escapeCell(it) })
            games.forEach { game ->
                appendLine(buildRow(game))
            }
        }

        val bytes = UTF8_BOM + csvBody.toByteArray(Charsets.UTF_8)
        file.writeBytes(bytes)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        return CsvExportResult(file = file, uri = uri, rowCount = games.size)
    }

    private fun buildRow(game: GameWithMaster): String {
        val master = game.gameMaster
        val item = game.collectionItem
        val cells = listOf(
            master.title,
            master.platform,
            master.publisher,
            master.releaseYear?.toString(),
            master.janCode,
            item.ownershipStatus.toLabel(),
            item.playStatus.toLabel(),
            item.rating?.toString(),
            item.visibility.toLabel(),
            item.purchasePrice?.toString(),
            item.purchaseStore,
            item.purchaseDate,
            item.purchaseCondition.toLabel(),
            item.purchaseMemo,
            item.notes,
            formatAddedAt(item.addedAt),
        )
        return cells.joinToString(",") { escapeCell(it) }
    }

    private fun escapeCell(value: String?): String {
        if (value.isNullOrEmpty()) return ""
        return if (value.contains(',') || value.contains('"') || value.contains('\n') || value.contains('\r')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun formatAddedAt(timestamp: Long): String =
        ADDED_AT_FORMAT.format(Date(timestamp))

    private fun timestamp(): String =
        FILE_NAME_FORMAT.format(Date())

    companion object {
        private val UTF8_BOM = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

        private val FILE_NAME_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        private val ADDED_AT_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val CSV_HEADERS = CsvColumnDefinitions.HEADERS
    }
}
