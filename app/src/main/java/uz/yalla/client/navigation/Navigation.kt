package uz.yalla.client.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.auth.AUTH_ROUTE
import uz.yalla.client.feature.auth.authModule
import uz.yalla.client.feature.auth.navigateToAuthModule
import uz.yalla.client.feature.bonus.bonusModule
import uz.yalla.client.feature.bonus.navigateToBonusModule
import uz.yalla.client.feature.contact.navigation.contactUsScreen
import uz.yalla.client.feature.contact.navigation.navigateToContactUsScreen
import uz.yalla.client.feature.history.historyModule
import uz.yalla.client.feature.history.navigateToHistoryModule
import uz.yalla.client.feature.info.about_app.navigation.aboutAppScreen
import uz.yalla.client.feature.info.about_app.navigation.navigateToAboutAppScreen
import uz.yalla.client.feature.intro.INTRO_ROUTE
import uz.yalla.client.feature.intro.introModule
import uz.yalla.client.feature.intro.navigateToIntroModel
import uz.yalla.client.feature.map.presentation.new_version.navigation.FromMap
import uz.yalla.client.feature.map.presentation.new_version.navigation.MAP_ROUTE
import uz.yalla.client.feature.map.presentation.new_version.navigation.mapScreen
import uz.yalla.client.feature.map.presentation.new_version.navigation.navigateToMapScreen
import uz.yalla.client.feature.notification.navigateToNotificationModule
import uz.yalla.client.feature.notification.notificationModule
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
import uz.yalla.client.ui.screens.OfflineScreen

@Composable
fun Navigation(isConnected: Boolean) {
    val staticPreferences = koinInject<StaticPreferences>()
    val navController = rememberNavController()
    var route by remember { mutableStateOf("") }
    var showOfflineByMap by remember { mutableStateOf(false) }

    LaunchedEffect(navController.currentDestination?.route) {
        route = navController.currentDestination?.route ?: ""
    }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = when {
            staticPreferences.isDeviceRegistered -> MAP_ROUTE
            staticPreferences.skipOnboarding -> AUTH_ROUTE
            else -> INTRO_ROUTE
        },
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(700)
            )
        }
    ) {
        introModule(
            navController = navController,
            onPermissionGranted = {
                if (staticPreferences.isDeviceRegistered) navController.navigateToMapScreen()
                else navController.navigateToAuthModule()
            }
        )

        authModule(
            navController = navController,
            onClientNotFound = navController::navigateToRegistrationScreen,
            onClientFound = navController::navigateToMapScreen
        )

        registrationScreen(
            onBack = navController::safePopBackStack,
            onNext = navController::navigateToMapScreen
        )

        mapScreen(
            networkState = isConnected,
            navigate = { fromMap ->
                when (fromMap) {
                    FromMap.ToAboutApp -> navController.navigateToAboutAppScreen()
                    FromMap.ToAddNewCard -> navController.navigateToPaymentModule()
                    FromMap.ToAddresses -> navController.navigateToAddressModule()
                    FromMap.ToBonuses -> navController.navigateToBonusModule()
                    FromMap.ToContactUs -> navController.navigateToContactUsScreen()
                    FromMap.ToNotifications -> navController.navigateToNotificationModule()
                    FromMap.ToOrderHistory -> navController.navigateToHistoryModule()
                    FromMap.ToPaymentType -> navController.navigateToPaymentModule()
                    FromMap.ToProfile -> navController.navigateToEditProfileScreen()
                    FromMap.ToRegister -> navController.navigateToAuthModule()
                    FromMap.ToSettings -> navController.navigateToSettings()
                    is FromMap.ToCancel -> navController.navigateToCancelReasonScreen(fromMap.orderId)
                    is FromMap.ToInviteFriend -> navController.navigateToWebScreen(
                        fromMap.title,
                        fromMap.url
                    )

                    is FromMap.ToBecomeDriver -> navController.navigateToWebScreen(
                        fromMap.title,
                        fromMap.url
                    )
                }
            }
        )

        bonusModule(navController = navController)

        historyModule(navController = navController)

        paymentModule(navController = navController)

        cancelReasonScreen(
            onNavigateBack = navController::navigateToMapScreen
        )

        addressModule(navController = navController)

        editProfileScreen(
            onNavigateBack = navController::safePopBackStack,
            onNavigateToStart = navController::navigateToIntroModel
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

        notificationModule(navController = navController)
    }

    if ((!isConnected && route != MAP_ROUTE) || (showOfflineByMap && route == MAP_ROUTE)) {
        OfflineScreen()
    }
}