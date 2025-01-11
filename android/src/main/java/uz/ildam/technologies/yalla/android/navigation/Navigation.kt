package uz.ildam.technologies.yalla.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.ildam.technologies.yalla.android.ui.screens.about_app.aboutAppScreen
import uz.ildam.technologies.yalla.android.ui.screens.about_app.navigateToAboutAppScreen
import uz.ildam.technologies.yalla.android.ui.screens.add_card.addCardScreen
import uz.ildam.technologies.yalla.android.ui.screens.add_card.navigateToAddCardScreen
import uz.ildam.technologies.yalla.android.ui.screens.address.addressScreen
import uz.ildam.technologies.yalla.android.ui.screens.address.navigateToAddressScreen
import uz.ildam.technologies.yalla.android.ui.screens.addresses.addressesScreen
import uz.ildam.technologies.yalla.android.ui.screens.addresses.navigateToAddressesScreen
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.cancelReasonScreen
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.navigateToCancelReasonScreen
import uz.ildam.technologies.yalla.android.ui.screens.card_list.cardListScreen
import uz.ildam.technologies.yalla.android.ui.screens.card_list.navigateToCardListScreen
import uz.ildam.technologies.yalla.android.ui.screens.card_verification.cardVerificationScreen
import uz.ildam.technologies.yalla.android.ui.screens.card_verification.navigateToCardVerificationScreen
import uz.ildam.technologies.yalla.android.ui.screens.contact_us.contactUsScreen
import uz.ildam.technologies.yalla.android.ui.screens.contact_us.navigateToContactUsScreen
import uz.ildam.technologies.yalla.android.ui.screens.credentials.credentialsScreen
import uz.ildam.technologies.yalla.android.ui.screens.credentials.navigateToCredentialsScreen
import uz.ildam.technologies.yalla.android.ui.screens.details.detailsScreen
import uz.ildam.technologies.yalla.android.ui.screens.details.navigateToDetailsScreen
import uz.ildam.technologies.yalla.android.ui.screens.edit_profile.editProfileScreen
import uz.ildam.technologies.yalla.android.ui.screens.edit_profile.navigateToEditProfileScreen
import uz.ildam.technologies.yalla.android.ui.screens.history.historyScreen
import uz.ildam.technologies.yalla.android.ui.screens.history.navigateToHistoryScreen
import uz.ildam.technologies.yalla.android.ui.screens.language.languageScreen
import uz.ildam.technologies.yalla.android.ui.screens.language.navigateToLanguageScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.MAP_ROUTE
import uz.ildam.technologies.yalla.android.ui.screens.map.mapScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.navigateToMapScreen
import uz.ildam.technologies.yalla.android.ui.screens.offline.OfflineScreen
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.ONBOARDING_ROUTE
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.onboardingScreen
import uz.ildam.technologies.yalla.android.ui.screens.permission.navigateToPermissionScreen
import uz.ildam.technologies.yalla.android.ui.screens.permission.permissionScreen
import uz.ildam.technologies.yalla.android.ui.screens.settings.navigateToSettings
import uz.ildam.technologies.yalla.android.ui.screens.settings.settingsScreen
import uz.ildam.technologies.yalla.android.ui.screens.verification.navigateToVerificationScreen
import uz.ildam.technologies.yalla.android.ui.screens.verification.verificationScreen
import uz.ildam.technologies.yalla.android.ui.screens.web.navigateToWebScreen
import uz.ildam.technologies.yalla.android.ui.screens.web.webScreen
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.android.auth.login.navigation.loginScreen
import uz.yalla.client.feature.android.auth.login.navigation.navigateToLoginScreen

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
            startDestination = if (AppPreferences.isDeviceRegistered) MAP_ROUTE else ONBOARDING_ROUTE
        ) {
            onboardingScreen(
                onNext = navController::navigateToPermissionScreen
            )

            permissionScreen(
                onPermissionGranted = {
                    if (AppPreferences.isDeviceRegistered) navController.navigateToMapScreen()
                    else navController.navigateToLanguageScreen()
                }
            )

            languageScreen(
                onBack = navController::safePopBackStack,
                onNext = navController::navigateToLoginScreen
            )

            loginScreen(
                onBack = navController::safePopBackStack,
                onNext = navController::navigateToVerificationScreen
            )



            credentialsScreen(
                onBack = navController::safePopBackStack,
                onNext = {
                    navController.navigateToMapScreen(
                        navOptions { popUpTo(0) { inclusive = true } }
                    )
                },
            )

            verificationScreen(
                onBack = navController::safePopBackStack,
                onClientFound = {
                    navController.navigateToMapScreen(
                        navOptions { popUpTo(0) { inclusive = true } }
                    )
                },
                onClientNotFound = navController::navigateToCredentialsScreen
            )

            mapScreen(
                onProfileClick = navController::navigateToEditProfileScreen,
                onOrderHistoryClick = navController::navigateToHistoryScreen,
                onPaymentTypeClick = navController::navigateToCardListScreen,
                onAddressesClick = navController::navigateToAddressesScreen,
                onSettingsClick = navController::navigateToSettings,
                onPermissionDenied = navController::navigateToPermissionScreen,
                onCancel = navController::navigateToCancelReasonScreen,
                onAddNewCard = navController::navigateToAddCardScreen,
                onAboutAppClick = navController::navigateToAboutAppScreen,
                onContactUsClick = navController::navigateToContactUsScreen,
                becomeDriverClick = navController::navigateToWebScreen,
                inviteFriendClick = navController::navigateToWebScreen
            )

            historyScreen(
                onBack = navController::safePopBackStack,
                onClickItem = navController::navigateToDetailsScreen
            )

            detailsScreen(
                onNavigateBack = navController::safePopBackStack
            )

            cardListScreen(
                onNavigateBack = navController::safePopBackStack,
                onAddNewCard = navController::navigateToAddCardScreen
            )

            addCardScreen(
                onNavigateBack = navController::safePopBackStack,
                onNavigateNext = navController::navigateToCardVerificationScreen
            )

            cardVerificationScreen(
                onNavigateBack = navController::navigateToCardListScreen
            )

            cancelReasonScreen(
                onNavigateBack = navController::safePopBackStack
            )

            addressesScreen(
                onNavigateBack = navController::safePopBackStack,
                onClickAddress = navController::navigateToAddressScreen,
                onAddAddress = navController::navigateToAddressScreen
            )

            addressScreen(
                onNavigateBack = navController::safePopBackStack
            )

            editProfileScreen(
                onNavigateBack = navController::safePopBackStack
            )

            settingsScreen(onNavigateBack = navController::safePopBackStack)

            aboutAppScreen(
                onBack = navController::safePopBackStack,
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