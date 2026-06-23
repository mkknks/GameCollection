package com.gamecollection.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gamecollection.di.LocalAppContainer
import com.gamecollection.ui.screen.BacklogListScreen
import com.gamecollection.ui.screen.BacklogRandomScreen
import com.gamecollection.ui.screen.BarcodeScanScreen
import com.gamecollection.ui.screen.GameDetailScreen
import com.gamecollection.ui.screen.GameListScreen
import com.gamecollection.ui.screen.ManualRegisterScreen
import com.gamecollection.ui.screen.MasterSearchScreen
import com.gamecollection.ui.viewmodel.BacklogListViewModel
import com.gamecollection.ui.viewmodel.BacklogRandomViewModel
import com.gamecollection.ui.viewmodel.BarcodeScanViewModel
import com.gamecollection.ui.viewmodel.GameDetailViewModel
import com.gamecollection.ui.viewmodel.GameListViewModel
import com.gamecollection.ui.viewmodel.ManualRegisterViewModel
import com.gamecollection.ui.viewmodel.MasterSearchViewModel

@Composable
fun GameCollectionNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val repository = LocalAppContainer.current.repository

    NavHost(
        navController = navController,
        startDestination = NavRoutes.GAME_LIST,
        modifier = modifier,
    ) {
        composable(NavRoutes.GAME_LIST) {
            val viewModel: GameListViewModel = viewModel(
                factory = GameListViewModel.Factory(repository),
            )
            GameListScreen(
                onGameClick = { collectionItemId ->
                    navController.navigate(NavRoutes.gameDetail(collectionItemId))
                },
                onManualRegisterClick = {
                    navController.navigate(NavRoutes.manualRegister())
                },
                onMasterSearchClick = {
                    navController.navigate(NavRoutes.MASTER_SEARCH)
                },
                onBacklogListClick = {
                    navController.navigate(NavRoutes.BACKLOG_LIST)
                },
                onBarcodeScanClick = {
                    navController.navigate(NavRoutes.BARCODE_SCAN)
                },
                viewModel = viewModel,
            )
        }

        composable(
            route = NavRoutes.GAME_DETAIL,
            arguments = listOf(
                navArgument("collectionItemId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val collectionItemId = backStackEntry.arguments?.getLong("collectionItemId") ?: return@composable
            val viewModel: GameDetailViewModel = viewModel(
                factory = GameDetailViewModel.Factory(repository, collectionItemId),
            )
            GameDetailScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }

        composable(
            route = NavRoutes.MANUAL_REGISTER,
            arguments = listOf(
                navArgument("janCode") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            val janCode = backStackEntry.arguments?.getString("janCode").orEmpty()
            val viewModel: ManualRegisterViewModel = viewModel(
                factory = ManualRegisterViewModel.Factory(repository, janCode),
            )
            ManualRegisterScreen(
                onBack = { navController.popBackStack() },
                onRegistered = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }

        composable(NavRoutes.MASTER_SEARCH) {
            val viewModel: MasterSearchViewModel = viewModel(
                factory = MasterSearchViewModel.Factory(repository),
            )
            MasterSearchScreen(
                onBack = { navController.popBackStack() },
                onRegistered = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }

        composable(NavRoutes.BACKLOG_LIST) {
            val viewModel: BacklogListViewModel = viewModel(
                factory = BacklogListViewModel.Factory(repository),
            )
            BacklogListScreen(
                onBack = { navController.popBackStack() },
                onGameClick = { collectionItemId ->
                    navController.navigate(NavRoutes.gameDetail(collectionItemId))
                },
                onRandomPickClick = {
                    navController.navigate(NavRoutes.BACKLOG_RANDOM)
                },
                viewModel = viewModel,
            )
        }

        composable(NavRoutes.BACKLOG_RANDOM) {
            val viewModel: BacklogRandomViewModel = viewModel(
                factory = BacklogRandomViewModel.Factory(repository),
            )
            BacklogRandomScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }

        composable(NavRoutes.BARCODE_SCAN) {
            val viewModel: BarcodeScanViewModel = viewModel(
                factory = BarcodeScanViewModel.Factory(repository),
            )
            BarcodeScanScreen(
                onBack = { navController.popBackStack() },
                onNavigateToManualRegister = { janCode ->
                    navController.navigate(NavRoutes.manualRegister(janCode))
                },
                onRegistered = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
            )
        }
    }
}
