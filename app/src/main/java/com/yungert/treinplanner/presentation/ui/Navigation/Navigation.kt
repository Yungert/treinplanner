package com.yungert.treinplanner.presentation.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yungert.treinplanner.presentation.ui.ComposeStaions
import com.yungert.treinplanner.presentation.ui.ShowDetailReisAdvies
import com.yungert.treinplanner.presentation.ui.ShowReisAdvies
import com.yungert.treinplanner.presentation.ui.ViewModel.DetailReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.StationVanKiezen.route){
        composable(route = Screen.StationVanKiezen.route){
            ComposeStaions(null, navController= navController)
        }

        composable(route = Screen.StationNaarKiezen.route + "/{vanstation}",
            arguments = listOf(
                navArgument("vanstation"){
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            ComposeStaions(vanStation = entry.arguments?.getString("vanstation") ?: "", navController= navController)
        }
        composable(route = Screen.Reisadvies.route + "/{vanstation}/{naarstation}",
            arguments = listOf(
                navArgument("vanstation"){
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("naarstation"){
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            var viewmodel : ReisAdviesViewModel = viewModel()
            ShowReisAdvies(
                vertrekStation = entry.arguments?.getString("vanstation") ?: "",
                eindStation = entry.arguments?.getString("naarstation") ?: "",
                viewModel = viewmodel,
                navController = navController
            )
        }
        composable(route = Screen.Reisadvies.route + "/{reisadviesId}",
            arguments = listOf(
                navArgument("reisadviesId"){
                    type = NavType.StringType
                    nullable = false
                }

            )
        ) { entry ->
            var viewmodel : DetailReisAdviesViewModel = viewModel()
            ShowDetailReisAdvies(reisADviesId = entry.arguments?.getString("reisadviesId") ?: "", viewModel = viewmodel, navController = navController)
        }
    }
}