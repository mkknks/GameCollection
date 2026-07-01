package com.gamecollection.export

data class CsvImportRow(
    val title: String,
    val platform: String?,
    val publisher: String?,
    val releaseYear: Int?,
    val janCode: String?,
    val ownershipStatusLabel: String,
    val playStatusLabel: String,
    val rating: Int?,
    val visibilityLabel: String,
    val purchasePrice: Int?,
    val purchaseStore: String?,
    val purchaseDate: String?,
    val purchaseConditionLabel: String,
    val purchaseMemo: String?,
    val notes: String?,
    val addedAtLabel: String?,
)

data class CsvParseResult(
    val rows: List<CsvImportRow>,
    val parseErrors: List<String>,
)
