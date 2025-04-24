package uz.yalla.client.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.yalla.client.feature.auth.authModule
import uz.yalla.client.feature.auth.navigateToAuthModule
import uz.yalla.client.feature.contact.navigation.contactUsScreen
import uz.yalla.client.feature.contact.navigation.navigateToContactUsScreen
import uz.yalla.client.feature.history.historyModule
import uz.yalla.client.feature.history.navigateToHistoryModule
import uz.yalla.client.feature.info.about_app.navigation.aboutAppScreen
import uz.yalla.client.feature.info.about_app.navigation.navigateToAboutAppScreen
import uz.yalla.client.feature.intro.INTRO_ROUTE
import uz.yalla.client.feature.intro.introModule
import uz.yalla.client.feature.intro.navigateToIntroModel
import uz.yalla.client.feature.map.presentation.navigation.MAP_ROUTE
import uz.yalla.client.feature.map.presentation.navigation.mapScreen
import uz.yalla.client.feature.map.presentation.navigation.navigateToMapScreen
import uz.yalla.client.feature.order.presentation.cancel_reason.cancelReasonScreen
import uz.yalla.client.feature.order.presentation.cancel_reason.navigateToCancelReasonScreen
import uz.yalla.client.feature.payment.navigateToPaymentModule
import uz.yalla.client.feature.payment.paymentModule
import uz.yalla.client.feature.places.addressModule
import uz.yalla.client.feature.places.navigateToAddressModule
import uz.yalla.client.feature.profile.edit_profile.navigation.editProfileScreen
import uz.yalla.client.feature.profile.edit_profile.navigation.navigateToEditProfileScreen
import uz.yalla.client.feature.registration.presentation.navigation.navigateToRegistrationScreen
import uz.yalla.client.feature.registration.presentation.navigation.registrationScreen
import uz.yalla.client.feature.setting.navigation.navigateToSettings
import uz.yalla.client.feature.setting.navigation.settingsScreen
import uz.yalla.client.feature.web.navigateToWebScreen
import uz.yalla.client.feature.web.webScreen
import uz.yalla.client.ui.screens.offline.OfflineScreen

@Composable
fun Navigation(
    isConnected: Boolean,
    isDeviceRegistered: Boolean
) {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = if (!isDeviceRegistered) INTRO_ROUTE else MAP_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) {
        introModule(
            navController = navController,
            onPermissionGranted = {
                if (isDeviceRegistered) {
                    navController.navigateToMapScreen(
                        navOptions {
                            restoreState = true
                            popUpTo(0) { inclusive = true }
                        }
                    )
                } else {
                    navController.navigateToAuthModule()
                }
            }
        )

        authModule(
            navController = navController,
            onClientNotFound = navController::navigateToRegistrationScreen,
            onClientFound = {
                navController.navigateToMapScreen(
                    navOptions {
                        restoreState = true
                        popUpTo(0) { inclusive = true }
                    }
                )
            }
        )

        registrationScreen(
            onBack = navController::safePopBackStack,
            onNext = {
                navController.navigateToMapScreen(
                    navOptions {
                        restoreState = true
                        popUpTo(0) { inclusive = true }
                    }
                )
            }
        )

        mapScreen(
            onProfileClick = navController::navigateToEditProfileScreen,
            onOrderHistoryClick = navController::navigateToHistoryModule,
            onPaymentTypeClick = navController::navigateToPaymentModule,
            onAddressesClick = navController::navigateToAddressModule,
            onSettingsClick = navController::navigateToSettings,
            onPermissionDenied = navController::navigateToIntroModel,
            onCancel = navController::navigateToCancelReasonScreen,
            onAddNewCard = navController::navigateToPaymentModule,
            onAboutAppClick = navController::navigateToAboutAppScreen,
            onContactUsClick = navController::navigateToContactUsScreen,
            becomeDriverClick = navController::navigateToWebScreen,
            inviteFriendClick = navController::navigateToWebScreen
        )

        historyModule(navController = navController)
        paymentModule(navController = navController)

        cancelReasonScreen(
            onNavigateBack = {
                navController.navigateToMapScreen(
                    navOptions {
                        restoreState = true
                        popUpTo(0) { inclusive = true }
                    }
                )
            }
        )

        addressModule(navController = navController)

        editProfileScreen(
            onNavigateBack = navController::safePopBackStack,
            onNavigateToStart = {
                navController.navigateToIntroModel(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                )
            }
        )

        settingsScreen(onNavigateBack = navController::safePopBackStack)

        aboutAppScreen(
            onBack = navController::safePopBackStack,
            onClickUrl = navController::navigateToWebScreen
        )

        contactUsScreen(
            onBack = navController::safePopBackStack,
            onClickUrl = navController::navigateToWebScreen
        )

        webScreen(onNavigateBack = navController::popBackStack)
    }

    if (!isConnected) {
        OfflineScreen()
    }
}