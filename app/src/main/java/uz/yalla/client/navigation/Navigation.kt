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
import uz.yalla.client.feature.bonus.bonusModule
import uz.yalla.client.feature.bonus.navigateToBonusModule
import uz.yalla.client.feature.contact.navigation.FromContactUs
import uz.yalla.client.feature.contact.navigation.contactUsScreen
import uz.yalla.client.feature.contact.navigation.navigateToContactUsScreen
import uz.yalla.client.feature.history.historyModule
import uz.yalla.client.feature.history.navigateToHistoryModule
import uz.yalla.client.feature.info.about_app.navigation.FromAboutApp
import uz.yalla.client.feature.info.about_app.navigation.aboutAppScreen
import uz.yalla.client.feature.info.about_app.navigation.navigateToAboutAppScreen
import uz.yalla.client.feature.map.presentation.new_version.navigation.FromMap
import uz.yalla.client.feature.map.presentation.new_version.navigation.MAP_ROUTE
import uz.yalla.client.feature.map.presentation.new_version.navigation.mapScreen
import uz.yalla.client.feature.notification.navigateToNotificationModule
import uz.yalla.client.feature.notification.notificationModule
import uz.yalla.client.feature.payment.navigateToPaymentModule
import uz.yalla.client.feature.payment.paymentModule
import uz.yalla.client.feature.places.addressModule
import uz.yalla.client.feature.places.navigateToAddressModule
import uz.yalla.client.feature.profile.edit_profile.navigation.FromEditProfile
import uz.yalla.client.feature.profile.edit_profile.navigation.editProfileScreen
import uz.yalla.client.feature.profile.edit_profile.navigation.navigateToEditProfileScreen
import uz.yalla.client.feature.promocode.presentation.add_promocode.navigation.FromAddPromocode
import uz.yalla.client.feature.promocode.presentation.add_promocode.navigation.addPromocodeScreen
import uz.yalla.client.feature.promocode.presentation.add_promocode.navigation.navigateToAddPromocodeScreen
import uz.yalla.client.feature.setting.navigation.FromSettings
import uz.yalla.client.feature.setting.navigation.navigateToSettings
import uz.yalla.client.feature.setting.navigation.settingsScreen
import uz.yalla.client.feature.web.navigateToWebScreen
import uz.yalla.client.feature.web.webScreen
import uz.yalla.client.ui.screens.OfflineScreen


@Composable
fun Navigation(
    isConnected: Boolean,
    navigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    var route by remember { mutableStateOf("") }

    LaunchedEffect(navController.currentDestination?.route) {
        route = navController.currentDestination?.route ?: ""
    }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = MAP_ROUTE,
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
                    FromMap.ToRegister -> navigateToLogin()
                    FromMap.ToSettings -> navController.navigateToSettings()

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

        bonusModule(
            navController = navController,
            onAddPromocodeClicked = navController::navigateToAddPromocodeScreen
        )

        historyModule(navController = navController)

        paymentModule(navController = navController)

        addressModule(navController = navController)

        editProfileScreen { fromEditProfile ->
            when (fromEditProfile) {
                FromEditProfile.NavigateBack -> navController.safePopBackStack()
                FromEditProfile.NavigateToLogin -> navigateToLogin()
            }
        }

        settingsScreen { fromSettings ->
            when (fromSettings) {
                FromSettings.NavigateBack -> navController.safePopBackStack()
            }
        }

        aboutAppScreen { fromAboutApp ->
            when (fromAboutApp) {
                FromAboutApp.NavigateBack -> navController.safePopBackStack()
                is FromAboutApp.ToWeb -> navController.navigateToWebScreen(
                    title = fromAboutApp.title,
                    url = fromAboutApp.url
                )
            }
        }

        contactUsScreen { fromContactUs ->
            when (fromContactUs) {
                FromContactUs.NavigateBack -> navController.safePopBackStack()
                is FromContactUs.ToWeb -> navController.navigateToWebScreen(
                    title = fromContactUs.title,
                    url = fromContactUs.url
                )
            }
        }

        webScreen(onBack = navController::safePopBackStack)

        notificationModule(navController = navController)

        addPromocodeScreen { fromAddPromocode ->
            when (fromAddPromocode) {
                FromAddPromocode.NavigateBack -> navController.safePopBackStack()
            }
        }
    }

    if (!isConnected && route != MAP_ROUTE) {
        OfflineScreen()
    }
}
