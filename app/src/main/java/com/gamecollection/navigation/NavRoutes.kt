package com.gamecollection.navigation

import android.net.Uri

object NavRoutes {
    const val GAME_LIST = "game_list"
    const val GAME_DETAIL = "game_detail/{collectionItemId}"
    const val MANUAL_REGISTER = "manual_register?janCode={janCode}"
    const val MASTER_SEARCH = "master_search"
    const val BACKLOG_LIST = "backlog_list"
    const val BACKLOG_RANDOM = "backlog_random"
    const val BARCODE_SCAN = "barcode_scan"
    const val CONTINUOUS_SCAN = "continuous_scan"
    const val CONTINUOUS_SCAN_RESULT = "continuous_scan_result"

    fun gameDetail(collectionItemId: Long): String = "game_detail/$collectionItemId"

    fun manualRegister(janCode: String = ""): String =
        "manual_register?janCode=${Uri.encode(janCode)}"
}
