package uz.ildam.technologies.yalla.android.ui.screens.card_verification

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val KEY = "key"
const val CARD_NUMBER = "card_number"
const val CARD_EXPIRY = "expires_in"
const val CARD_VERIFICATION_ROUTE_BASE = "card_verification_route"
const val CARD_VERIFICATION_ROUTE =
    "$CARD_VERIFICATION_ROUTE_BASE?$KEY={$KEY}&$CARD_NUMBER={$CARD_NUMBER}&$CARD_EXPIRY={$CARD_EXPIRY}"

fun NavGraphBuilder.cardVerificationScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = CARD_VERIFICATION_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        arguments = listOf(
            navArgument(KEY) { type = NavType.StringType },
            navArgument(CARD_NUMBER) { type = NavType.StringType },
            navArgument(CARD_EXPIRY) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        CardVerificationRoute(
            key = backStackEntry.arguments?.getString(KEY).orEmpty(),
            cardNumber = backStackEntry.arguments?.getString(CARD_NUMBER).orEmpty(),
            cardExpiry = backStackEntry.arguments?.getString(CARD_EXPIRY).orEmpty(),
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToCardVerificationScreen(
    key: String,
    cardNumber: String,
    cardExpiry: String,
    navOptions: NavOptions? = null
) {
    val route =
        "$CARD_VERIFICATION_ROUTE_BASE?${KEY}=$key&${CARD_NUMBER}=$cardNumber&${CARD_EXPIRY}=$cardExpiry"
    navigate(route, navOptions)
}