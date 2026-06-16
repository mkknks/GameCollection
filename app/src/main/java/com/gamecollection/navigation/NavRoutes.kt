package com.gamecollection.navigation

object NavRoutes {
    const val GAME_LIST = "game_list"
    const val GAME_DETAIL = "game_detail/{collectionItemId}"
    const val MANUAL_REGISTER = "manual_register"
    const val MASTER_SEARCH = "master_search"

    fun gameDetail(collectionItemId: Long): String = "game_detail/$collectionItemId"
}
