package uz.yalla.client.feature.payment.card_verification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.payment.card_verification.view.CardVerificationRoute

internal const val KEY = "key"
internal const val CARD_NUMBER = "card_number"
internal const val CARD_EXPIRY = "expires_in"
internal const val CARD_VERIFICATION_ROUTE_BASE = "card_verification_route"
internal const val CARD_VERIFICATION_ROUTE =
    "$CARD_VERIFICATION_ROUTE_BASE?$KEY={$KEY}&$CARD_NUMBER={$CARD_NUMBER}&$CARD_EXPIRY={$CARD_EXPIRY}"

internal fun NavGraphBuilder.cardVerificationScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = CARD_VERIFICATION_ROUTE,
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
        "$CARD_VERIFICATION_ROUTE_BASE?$KEY=$key&$CARD_NUMBER=$cardNumber&$CARD_EXPIRY=$cardExpiry"
    safeNavigate(route, navOptions)
}