package com.gamecollection.data.repository

import com.gamecollection.data.model.OwnershipStatus
import com.gamecollection.data.model.PlayStatus
import com.gamecollection.data.model.Visibility

data class BulkUpdateParams(
    val ownershipStatus: OwnershipStatus,
    val playStatus: PlayStatus,
    val visibility: Visibility,
)
