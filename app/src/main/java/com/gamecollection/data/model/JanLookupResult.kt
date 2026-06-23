package com.gamecollection.data.model

import com.gamecollection.data.entity.GameMasterEntity

sealed class JanLookupResult {
    data class Found(val gameMaster: GameMasterEntity) : JanLookupResult()
    data class AlreadyRegistered(val gameMaster: GameMasterEntity) : JanLookupResult()
    data class NotFound(val janCode: String) : JanLookupResult()
    data class Multiple(val games: List<GameMasterEntity>) : JanLookupResult()
}
