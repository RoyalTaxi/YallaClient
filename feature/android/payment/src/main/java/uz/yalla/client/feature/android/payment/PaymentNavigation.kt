package uz.yalla.client.feature.android.payment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.payment.add_card.navigation.addCardScreen
import uz.yalla.client.feature.android.payment.add_card.navigation.navigateToAddCardScreen
import uz.yalla.client.feature.android.payment.add_employee.navigation.addEmployeeScreen
import uz.yalla.client.feature.android.payment.add_employee.navigation.navigateToAddEmployee
import uz.yalla.client.feature.android.payment.business_account.navigation.businessAccountScreen
import uz.yalla.client.feature.android.payment.business_account.navigation.navigateToBusinessAccount
import uz.yalla.client.feature.android.payment.card_list.navigation.CARD_LIST_ROUTE
import uz.yalla.client.feature.android.payment.card_list.navigation.cardListScreen
import uz.yalla.client.feature.android.payment.card_list.navigation.navigateToCardListScreen
import uz.yalla.client.feature.android.payment.card_verification.navigation.cardVerificationScreen
import uz.yalla.client.feature.android.payment.card_verification.navigation.navigateToCardVerificationScreen
import uz.yalla.client.feature.android.payment.corporate_account.navigation.corporateAccountScreen
import uz.yalla.client.feature.android.payment.corporate_account.navigation.navigateToCorporateAccountScreen
import uz.yalla.client.feature.android.payment.employee.navigation.employeeScreen
import uz.yalla.client.feature.android.payment.employee.navigation.navigateToEmployee
import uz.yalla.client.feature.android.payment.top_up_balance.navigation.navigateToTopUpScreen
import uz.yalla.client.feature.android.payment.top_up_balance.navigation.topUpScreen
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
            onAddNewCard = navController::navigateToAddCardScreen,
            onAddCompany = navController::navigateToCorporateAccountScreen,
            onAddBusinessAccount = navController::navigateToBusinessAccount
        )

        addCardScreen(
            onNavigateBack = navController::safePopBackStack,
            onNavigateNext = navController::navigateToCardVerificationScreen
        )

        cardVerificationScreen(
            onNavigateBack = navController::navigateToCardListScreen
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