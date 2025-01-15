package uz.ildam.technologies.yalla.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.ildam.technologies.yalla.android.ui.screens.add_card.navigateToAddCardScreen
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.cancelReasonScreen
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.navigateToCancelReasonScreen
import uz.ildam.technologies.yalla.android.ui.screens.contact_us.contactUsScreen
import uz.ildam.technologies.yalla.android.ui.screens.contact_us.navigateToContactUsScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.MAP_ROUTE
import uz.ildam.technologies.yalla.android.ui.screens.map.mapScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.navigateToMapScreen
import uz.ildam.technologies.yalla.android.ui.screens.offline.OfflineScreen
import uz.ildam.technologies.yalla.android.ui.screens.web.navigateToWebScreen
import uz.ildam.technologies.yalla.android.ui.screens.web.webScreen
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.auth.authModule
import uz.yalla.client.feature.android.auth.navigateToAuthModule
import uz.yalla.client.feature.android.history.historyModule
import uz.yalla.client.feature.android.history.navigateToHistoryModule
import uz.yalla.client.feature.android.info.infoModule
import uz.yalla.client.feature.android.info.navigateToInfoModule
import uz.yalla.client.feature.android.intro.INTRO_ROUTE
import uz.yalla.client.feature.android.intro.introModule
import uz.yalla.client.feature.android.intro.navigateToIntroModel
import uz.yalla.client.feature.android.payment.navigateToPaymentModule
import uz.yalla.client.feature.android.payment.paymentModule
import uz.yalla.client.feature.android.places.addressModule
import uz.yalla.client.feature.android.places.navigateToAddressModule
import uz.yalla.client.feature.android.profile.navigateToProfileModule
import uz.yalla.client.feature.android.profile.profileModule
import uz.yalla.client.feature.android.registration.navigateToRegistrationModule
import uz.yalla.client.feature.android.registration.registrationModule
import uz.yalla.client.feature.android.setting.navigateToSettingModule
import uz.yalla.client.feature.android.setting.settingsModule

@Composable
fun Navigation(
    isConnected: Boolean
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = if (AppPreferences.isDeviceRegistered) MAP_ROUTE else INTRO_ROUTE
        ) {

            introModule(
                navController = navController,
                onPermissionGranted = navController::navigateToAuthModule,
            )

            authModule(
                navController = navController,
                onClientNotFound = navController::navigateToRegistrationModule,
                onClientFound = {
                    navController.navigateToMapScreen(
                        navOptions {
                            popUpTo(0) { inclusive = true }
                        }
                    )
                }
            )

            registrationModule(
                navController = navController,
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
                onAddNewCard = navController::navigateToAddCardScreen,
                onAboutAppClick = navController::navigateToInfoModule,
                onContactUsClick = navController::navigateToContactUsScreen,
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
                navController= navController
            )

            settingsModule(
                navController = navController
            )

            infoModule(
                navController = navController,
                onClickUrl = navController::navigateToWebScreen
            )

            contactUsScreen(
                onBack = navController::safePopBackStack,
                onClickUrl = navController::navigateToWebScreen,
            )

            webScreen(
                onNavigateBack = navController::safePopBackStack
            )
        }
    }

    if (isConnected.not()) OfflineScreen()
}