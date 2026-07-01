package com.gamecollection.export

import android.content.Context
import android.net.Uri
import com.gamecollection.data.repository.GameRepository
import java.text.SimpleDateFormat
import java.util.Locale

class CsvImporter(
    private val context: Context,
) {
    fun parse(uri: Uri): CsvParseResult {
        val text = readText(uri)
        return parseText(text)
    }

    fun parseText(text: String): CsvParseResult {
        val decoded = stripBom(text)
        if (decoded.isBlank()) {
            return CsvParseResult(emptyList(), listOf("CSVファイルが空です"))
        }

        val records = parseCsvRecords(decoded)
        if (records.isEmpty()) {
            return CsvParseResult(emptyList(), listOf("CSVにデータ行がありません"))
        }

        val header = records.first()
        if (!isValidHeader(header)) {
            return CsvParseResult(
                emptyList(),
                listOf("CSVのヘッダー形式が正しくありません"),
            )
        }

        val columnIndex = CsvColumnDefinitions.HEADERS.withIndex().associate { (index, name) ->
            name to index
        }

        val rows = mutableListOf<CsvImportRow>()
        val errors = mutableListOf<String>()

        records.drop(1).forEachIndexed { index, record ->
            val rowNumber = index + 2
            if (record.all { it.isBlank() }) return@forEachIndexed

            runCatching {
                rows.add(mapRecord(record, columnIndex))
            }.onFailure { error ->
                errors.add("${rowNumber}行目: ${error.message ?: "解析に失敗しました"}")
            }
        }

        return CsvParseResult(rows = rows, parseErrors = errors)
    }

    private fun readText(uri: Uri): String {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val bytes = input.readBytes()
            return bytes.toString(Charsets.UTF_8)
        } ?: throw IllegalArgumentException("CSVファイルを読み込めませんでした")
    }

    private fun stripBom(text: String): String {
        return if (text.isNotEmpty() && text[0] == '\uFEFF') {
            text.substring(1)
        } else {
            text
        }
    }

    private fun isValidHeader(header: List<String>): Boolean {
        if (header.size < CsvColumnDefinitions.HEADERS.size) return false
        return CsvColumnDefinitions.HEADERS.withIndex().all { (index, expected) ->
            header[index].trim() == expected
        }
    }

    private fun mapRecord(
        record: List<String>,
        columnIndex: Map<String, Int>,
    ): CsvImportRow {
        fun cell(name: String): String {
            val index = columnIndex[name] ?: return ""
            return record.getOrElse(index) { "" }.trim()
        }

        val title = cell("タイトル")
        if (title.isBlank()) {
            throw IllegalArgumentException("タイトルが空です")
        }

        val releaseYearRaw = cell("発売年")
        val releaseYear = if (releaseYearRaw.isEmpty()) {
            null
        } else {
            releaseYearRaw.toIntOrNull()
                ?: throw IllegalArgumentException("発売年が不正です")
        }

        val ratingRaw = cell("評価")
        val rating = if (ratingRaw.isEmpty()) {
            null
        } else {
            ratingRaw.toIntOrNull()
                ?: throw IllegalArgumentException("評価が不正です")
        }

        val purchasePriceRaw = cell("購入価格")
        val purchasePrice = if (purchasePriceRaw.isEmpty()) {
            null
        } else {
            purchasePriceRaw.toIntOrNull()
                ?: throw IllegalArgumentException("購入価格が不正です")
        }

        val janRaw = cell("JANコード")
        val janCode = janRaw.takeIf { it.isNotEmpty() }?.let { raw ->
            GameRepository.normalizeJanCode(raw) ?: throw IllegalArgumentException("JANコードが不正です")
        }

        return CsvImportRow(
            title = title,
            platform = cell("プラットフォーム").takeIf { it.isNotEmpty() },
            publisher = cell("発売元").takeIf { it.isNotEmpty() },
            releaseYear = releaseYear,
            janCode = janCode,
            ownershipStatusLabel = cell("所持状態"),
            playStatusLabel = cell("プレイ状態"),
            rating = rating,
            visibilityLabel = cell("公開設定"),
            purchasePrice = purchasePrice,
            purchaseStore = cell("購入店舗").takeIf { it.isNotEmpty() },
            purchaseDate = cell("購入日").takeIf { it.isNotEmpty() },
            purchaseConditionLabel = cell("購入状態"),
            purchaseMemo = cell("購入メモ").takeIf { it.isNotEmpty() },
            notes = cell("メモ").takeIf { it.isNotEmpty() },
            addedAtLabel = cell("追加日時").takeIf { it.isNotEmpty() },
        )
    }

    private fun parseCsvRecords(content: String): List<List<String>> {
        val records = mutableListOf<List<String>>()
        val currentRecord = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var index = 0

        while (index < content.length) {
            val char = content[index]
            when {
                inQuotes -> {
                    when (char) {
                        '"' -> {
                            if (index + 1 < content.length && content[index + 1] == '"') {
                                currentField.append('"')
                                index++
                            } else {
                                inQuotes = false
                            }
                        }
                        else -> currentField.append(char)
                    }
                }
                char == '"' -> inQuotes = true
                char == ',' -> {
                    currentRecord.add(currentField.toString())
                    currentField.clear()
                }
                char == '\r' -> Unit
                char == '\n' -> {
                    currentRecord.add(currentField.toString())
                    currentField.clear()
                    records.add(currentRecord.toList())
                    currentRecord.clear()
                }
                else -> currentField.append(char)
            }
            index++
        }

        if (currentField.isNotEmpty() || currentRecord.isNotEmpty()) {
            currentRecord.add(currentField.toString())
            records.add(currentRecord.toList())
        }

        return records
    }

    companion object {
        private val ADDED_AT_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun parseAddedAt(value: String?): Long? {
            if (value.isNullOrBlank()) return null
            return runCatching { ADDED_AT_FORMAT.parse(value)?.time }.getOrNull()
        }
    }
}
