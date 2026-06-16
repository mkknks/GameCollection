package com.gamecollection.ui.model

import com.gamecollection.data.model.CollectionStatus

fun CollectionStatus.toLabel(): String = when (this) {
    CollectionStatus.OWNED -> "所持"
    CollectionStatus.PLAYING -> "プレイ中"
    CollectionStatus.COMPLETED -> "クリア"
    CollectionStatus.WISHLIST -> "欲しい"
}
