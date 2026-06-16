package com.systemadminbd.syshackbar.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.systemadminbd.syshackbar.ui.screens.AboutScreen
import com.systemadminbd.syshackbar.ui.screens.BrowserScreen
import com.systemadminbd.syshackbar.ui.screens.CategoryScreen
import com.systemadminbd.syshackbar.ui.screens.EncoderScreen
import com.systemadminbd.syshackbar.ui.screens.HashScreen
import com.systemadminbd.syshackbar.ui.screens.HomeScreen
import com.systemadminbd.syshackbar.ui.screens.MyPayloadsScreen
import com.systemadminbd.syshackbar.ui.screens.ReplacerScreen
import com.systemadminbd.syshackbar.ui.screens.StringToolsScreen
import com.systemadminbd.syshackbar.ui.screens.TamperScreen
import com.systemadminbd.syshackbar.ui.screens.WebToolDetailScreen
import com.systemadminbd.syshackbar.ui.screens.WebToolsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Box(Modifier.fillMaxSize()) {
        NavHost(
                navController = navController,
                startDestination = "browser",
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(260))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(260))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(260))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(260))
                }
            ) {
                composable("browser") { BrowserScreen(navController = navController) }
                composable("home") { HomeScreen(navController = navController) }
                composable("web") { WebToolsScreen(navController = navController) }
                composable("saved") { MyPayloadsScreen(navController = navController) }
                composable("about") { AboutScreen(navController = navController) }
                composable("encoder") { EncoderScreen(navController = navController) }
                composable("hash") { HashScreen(navController = navController) }
                composable("replacer") { ReplacerScreen(navController = navController) }
                composable("strtools") { StringToolsScreen(navController = navController) }
                composable("tamper") { TamperScreen(navController = navController) }
                composable(
                    route = "category/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    CategoryScreen(navController = navController, categoryId = entry.arguments?.getString("id") ?: "")
                }
                composable(
                    route = "webtool/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    WebToolDetailScreen(navController = navController, toolId = entry.arguments?.getString("id") ?: "")
                }
        }
    }
}
