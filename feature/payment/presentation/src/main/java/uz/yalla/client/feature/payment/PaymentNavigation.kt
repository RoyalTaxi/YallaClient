package uz.yalla.client.feature.payment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.payment.add_card.navigation.addCardScreen
import uz.yalla.client.feature.payment.add_card.navigation.navigateToAddCardScreen
import uz.yalla.client.feature.payment.add_employee.navigation.addEmployeeScreen
import uz.yalla.client.feature.payment.add_employee.navigation.navigateToAddEmployee
import uz.yalla.client.feature.payment.business_account.navigation.businessAccountScreen
import uz.yalla.client.feature.payment.business_account.navigation.navigateToBusinessAccount
import uz.yalla.client.feature.payment.card_list.navigation.CARD_LIST_ROUTE
import uz.yalla.client.feature.payment.card_list.navigation.cardListScreen
import uz.yalla.client.feature.payment.card_list.navigation.navigateToCardListScreen
import uz.yalla.client.feature.payment.card_verification.navigation.cardVerificationScreen
import uz.yalla.client.feature.payment.card_verification.navigation.navigateToCardVerificationScreen
import uz.yalla.client.feature.payment.corporate_account.navigation.corporateAccountScreen
import uz.yalla.client.feature.payment.corporate_account.navigation.navigateToCorporateAccountScreen
import uz.yalla.client.feature.payment.employee.navigation.employeeScreen
import uz.yalla.client.feature.payment.employee.navigation.navigateToEmployee
import uz.yalla.client.feature.payment.top_up_balance.navigation.navigateToTopUpScreen
import uz.yalla.client.feature.payment.top_up_balance.navigation.topUpScreen

internal const val PAYMENT_ROUTE = "payment_route"

fun NavGraphBuilder.paymentModule(
    navController: NavHostController
) {
    navigation(
        startDestination = CARD_LIST_ROUTE,
        route = PAYMENT_ROUTE
    ) {
        cardListScreen(
            onBack = navController::safePopBackStack,
            onAddNewCard = navController::navigateToAddCardScreen,
            onAddCompany = navController::navigateToCorporateAccountScreen,
            onAddBusinessAccount = navController::navigateToBusinessAccount
        )

        addCardScreen(
            onNavigateBack = navController::safePopBackStack,
            onNavigateNext = navController::navigateToCardVerificationScreen
        )

        cardVerificationScreen(
            onNavigateBack = {
                navController.navigateToCardListScreen(
                    navOptions {
                        restoreState = true
                        popUpTo(PAYMENT_ROUTE) { inclusive = true }
                    }
                )
            }
        )

        corporateAccountScreen(
            onNavigateBack = navController::safePopBackStack
        )

        businessAccountScreen(
            onNavigateBack = navController::safePopBackStack,
            onClickEmployee = navController::navigateToEmployee,
            onClickAddBalance = navController::navigateToTopUpScreen,
            onClickAddEmployee = navController::navigateToAddEmployee
        )

        employeeScreen(
            onNavigateBack = navController::safePopBackStack,
            addBalance = navController::navigateToTopUpScreen
        )

        topUpScreen(
            onNavigateBack = navController::safePopBackStack
        )

        addEmployeeScreen(
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToPaymentModule() = safeNavigate(PAYMENT_ROUTE)