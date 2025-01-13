package uz.yalla.client.feature.android.payment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.payment.add_card.navigation.addCardScreen
import uz.yalla.client.feature.android.payment.add_card.navigation.navigateToAddCardScreen
import uz.yalla.client.feature.android.payment.card_list.navigation.CARD_LIST_ROUTE
import uz.yalla.client.feature.android.payment.card_list.navigation.cardListScreen
import uz.yalla.client.feature.android.payment.card_list.navigation.navigateToCardListScreen
import uz.yalla.client.feature.android.payment.card_verification.navigation.cardVerificationScreen
import uz.yalla.client.feature.android.payment.card_verification.navigation.navigateToCardVerificationScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

internal const val PAYMENT_ROUTE = "payment_route"

fun NavGraphBuilder.paymentModule(
    navController: NavHostController
){
    navigation(
        startDestination = CARD_LIST_ROUTE,
        route = PAYMENT_ROUTE
    ){
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
    }
}

fun NavController.navigateToPaymentModule() = safeNavigate(PAYMENT_ROUTE)