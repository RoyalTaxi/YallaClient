package uz.ildam.technologies.yalla.android.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.ildam.technologies.yalla.android.ui.screens.offline.OfflineScreen
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.auth.authModule
import uz.yalla.client.feature.auth.navigateToAuthModule
import uz.yalla.client.feature.contact.contactModule
import uz.yalla.client.feature.contact.navigateToContactModule
import uz.yalla.client.feature.history.historyModule
import uz.yalla.client.feature.history.navigateToHistoryModule
import uz.yalla.client.feature.info.infoModule
import uz.yalla.client.feature.info.navigateToInfoModule
import uz.yalla.client.feature.intro.INTRO_ROUTE
import uz.yalla.client.feature.intro.introModule
import uz.yalla.client.feature.intro.navigateToIntroModel
import uz.yalla.client.feature.payment.navigateToPaymentModule
import uz.yalla.client.feature.payment.paymentModule
import uz.yalla.client.feature.places.addressModule
import uz.yalla.client.feature.places.navigateToAddressModule
import uz.yalla.client.feature.profile.navigateToProfileModule
import uz.yalla.client.feature.profile.profileModule
import uz.yalla.client.feature.setting.navigateToSettingModule
import uz.yalla.client.feature.setting.settingsModule
import uz.yalla.client.feature.web.navigateToWebScreen
import uz.yalla.client.feature.web.webScreen
import uz.yalla.client.feature.map.presentation.navigation.MAP_ROUTE
import uz.yalla.client.feature.map.presentation.navigation.mapScreen
import uz.yalla.client.feature.map.presentation.navigation.navigateToMapScreen
import uz.yalla.client.feature.order.presentation.cancel.cancelReasonScreen
import uz.yalla.client.feature.order.presentation.cancel.navigateToCancelReasonScreen
import uz.yalla.client.feature.registration.presentation.navigation.navigateToRegistrationScreen
import uz.yalla.client.feature.registration.presentation.navigation.registrationScreen

@Composable
fun Navigation(
    isConnected: Boolean
) {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = if (AppPreferences.isDeviceRegistered) MAP_ROUTE else INTRO_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) {

        introModule(
            navController = navController,
            onPermissionGranted = navController::navigateToAuthModule,
        )

        authModule(
            navController = navController,
            onClientNotFound = navController::navigateToRegistrationScreen,
            onClientFound = {
                navController.navigateToMapScreen(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                    }
                )
            }
        )

        registrationScreen(
            onBack = navController::safePopBackStack,
            onNext = {
                navController.navigateToMapScreen(
                    navOptions { popUpTo(0) { inclusive = true } }
                )
            }
        )

        mapScreen(
            onProfileClick = navController::navigateToProfileModule,
            onOrderHistoryClick = navController::navigateToHistoryModule,
            onPaymentTypeClick = navController::navigateToPaymentModule,
            onAddressesClick = navController::navigateToAddressModule,
            onSettingsClick = navController::navigateToSettingModule,
            onPermissionDenied = navController::navigateToIntroModel,
            onCancel = navController::navigateToCancelReasonScreen,
            onAddNewCard = navController::navigateToPaymentModule,
            onAboutAppClick = navController::navigateToInfoModule,
            onContactUsClick = navController::navigateToContactModule,
            becomeDriverClick = navController::navigateToWebScreen,
            inviteFriendClick = navController::navigateToWebScreen
        )

        historyModule(
            navController = navController
        )

        paymentModule(
            navController = navController
        )

        cancelReasonScreen(
            onNavigateBack = navController::safePopBackStack
        )

        addressModule(
            navController = navController
        )

        profileModule(
            navController = navController,
            onNavigateToStart = {
                navController.navigateToIntroModel(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                )
            }
        )

        settingsModule(
            navController = navController
        )

        infoModule(
            navController = navController,
            onClickUrl = navController::navigateToWebScreen
        )

        contactModule(
            navController = navController,
            onClickUrl = navController::navigateToWebScreen
        )

        webScreen(
            onNavigateBack = navController::popBackStack
        )

    }
    if (isConnected.not()) OfflineScreen()
}