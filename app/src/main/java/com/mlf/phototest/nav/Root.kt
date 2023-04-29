package com.mlf.phototest.nav

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mlf.phototest.view.*


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavigation() {
    val rootNavController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = rootNavController,
        startDestination = RootDestination.Main.route,
        enterTransition = { fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End) }
    ) {
        composable(route = RootDestination.Main.route) {
            MainScreen(
                onNavigateToPermit = { rootNavController.navigate(RootDestination.Permit.route) },
                onNavigateToCamera = { rootNavController.navigate(RootDestination.Camera.route) },
                onNavigateToCheck = { rootNavController.navigate("${RootDestination.Check.route}/$it") }
            )
        }
        composable(route = RootDestination.Permit.route) {
            PermitScreen(
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
        composable(
            route = RootDestination.Check.routeWithArgs,
            arguments = RootDestination.Check.args
        ) {
            val backStackEntry = rootNavController.currentBackStackEntry
            val photoId = backStackEntry?.arguments?.getLong(RootDestination.Check.argPhotoId) ?: 0
            CheckScreen(
                photoId = photoId,
                onNavigateBack = { rootNavController.popBackStack() },
                onNavigateToCrop = { rootNavController.navigate("${RootDestination.Crop.route}/$it") },
                onNavigateToOcr = { rootNavController.navigate("${RootDestination.Ocr.route}/$it") },
                onNavigateToSr = { rootNavController.navigate("${RootDestination.Sr.route}/$it") }
            )
        }
        composable(
            route = RootDestination.Crop.routeWithArgs,
            arguments = RootDestination.Crop.args
        ) {
            val backStackEntry = rootNavController.currentBackStackEntry
            val photoId = backStackEntry?.arguments?.getLong(RootDestination.Crop.argPhotoId) ?: 0
            CropScreen(
                photoId = photoId,
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
        composable(
            route = RootDestination.Ocr.routeWithArgs,
            arguments = RootDestination.Ocr.args
        ) {
            val backStackEntry = rootNavController.currentBackStackEntry
            val photoId = backStackEntry?.arguments?.getLong(RootDestination.Ocr.argPhotoId) ?: 0
            OcrScreen(
                photoId = photoId,
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
        composable(
            route = RootDestination.Sr.routeWithArgs,
            arguments = RootDestination.Sr.args
        ) {
            val backStackEntry = rootNavController.currentBackStackEntry
            val photoId = backStackEntry?.arguments?.getLong(RootDestination.Sr.argPhotoId) ?: 0
            SrScreen(
                photoId = photoId,
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
        composable(route = RootDestination.Camera.route) {
            CameraScreen(
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
    }
}

sealed class RootDestination(
    val route: String
) {
    object Main : RootDestination("main")

    object Permit : RootDestination("permit")

    object Check : RootDestination("check") {
        const val argPhotoId = "photoId"

        val routeWithArgs = "$route/{$argPhotoId}"
        val args = listOf(
            navArgument(argPhotoId) { type = NavType.LongType }
        )
    }

    object Crop : RootDestination("crop") {
        const val argPhotoId = "photoId"

        val routeWithArgs = "$route/{$argPhotoId}"
        val args = listOf(
            navArgument(argPhotoId) { type = NavType.LongType }
        )
    }

    object Ocr : RootDestination("ocr") {
        const val argPhotoId = "photoId"

        val routeWithArgs = "$route/{$argPhotoId}"
        val args = listOf(
            navArgument(argPhotoId) { type = NavType.LongType }
        )
    }

    object Sr : RootDestination("sr") {
        const val argPhotoId = "photoId"

        val routeWithArgs = "$route/{$argPhotoId}"
        val args = listOf(
            navArgument(argPhotoId) { type = NavType.LongType }
        )
    }

    object Camera : RootDestination("camera")
}
