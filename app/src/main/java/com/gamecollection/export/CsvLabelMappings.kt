package com.gamecollection.export

import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.PurchaseCondition
import com.gamecollection.data.model.Visibility

object CsvLabelMappings {
    fun ownershipFromLabel(label: String): OwnershipStatus? = when (label.trim()) {
        "欲しい" -> OwnershipStatus.WISHLIST
        "所持中", "所持" -> OwnershipStatus.OWNING
        "売却済み" -> OwnershipStatus.SOLD
        else -> null
    }

    fun playStatusFromLabel(label: String): PlayStatus? = when (label.trim()) {
        "未プレイ" -> PlayStatus.NOT_PLAYED
        "プレイ中" -> PlayStatus.PLAYING
        "クリア済み", "クリア" -> PlayStatus.COMPLETED
        "中断" -> PlayStatus.PAUSED
        "積みゲー" -> PlayStatus.BACKLOG
        else -> null
    }

    fun visibilityFromLabel(label: String): Visibility? = when (label.trim()) {
        "公開" -> Visibility.PUBLIC
        "非公開" -> Visibility.PRIVATE
        else -> null
    }

    fun purchaseConditionFromLabel(label: String): PurchaseCondition? = when (label.trim()) {
        "新品" -> PurchaseCondition.NEW
        "中古" -> PurchaseCondition.USED
        "不明" -> PurchaseCondition.UNKNOWN
        "" -> PurchaseCondition.UNKNOWN
        else -> null
    }
}
