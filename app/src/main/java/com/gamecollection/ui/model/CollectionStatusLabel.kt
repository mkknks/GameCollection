package com.gamecollection.ui.model

import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility

fun OwnershipStatus.toLabel(): String = when (this) {
    OwnershipStatus.WISHLIST -> "欲しい"
    OwnershipStatus.OWNING -> "所持中"
    OwnershipStatus.SOLD -> "売却済み"
}

fun PlayStatus.toLabel(): String = when (this) {
    PlayStatus.NOT_PLAYED -> "未プレイ"
    PlayStatus.PLAYING -> "プレイ中"
    PlayStatus.COMPLETED -> "クリア済み"
    PlayStatus.PAUSED -> "中断"
    PlayStatus.BACKLOG -> "積みゲー"
}

fun PurchaseCondition.toLabel(): String = when (this) {
    PurchaseCondition.NEW -> "新品"
    PurchaseCondition.USED -> "中古"
    PurchaseCondition.UNKNOWN -> "不明"
}

fun Visibility.toLabel(): String = when (this) {
    Visibility.PUBLIC -> "公開"
    Visibility.PRIVATE -> "非公開"
}

sealed class RatingFilter {
    data object All : RatingFilter()
    data object Unrated : RatingFilter()
    data class Exact(val value: Int) : RatingFilter()
}

fun RatingFilter.toLabel(): String = when (this) {
    RatingFilter.All -> "すべて"
    RatingFilter.Unrated -> "未評価"
    is RatingFilter.Exact -> "${value}点"
}
