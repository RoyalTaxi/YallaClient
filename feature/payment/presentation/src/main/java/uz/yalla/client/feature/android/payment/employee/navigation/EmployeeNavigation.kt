package uz.yalla.client.feature.android.payment.employee.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.payment.employee.view.EmployeeIntent
import uz.yalla.client.feature.android.payment.employee.view.EmployeeScreen

internal const val EMPLOYEE_ROUTE = "employee_route"

internal fun NavGraphBuilder.employeeScreen(
    onNavigateBack: () -> Unit,
    addBalance: () -> Unit
) {
    composable(
        route = EMPLOYEE_ROUTE
    ) {

        EmployeeScreen(
            onIntent = {intent ->
                when (intent) {
                    EmployeeIntent.OnNavigateBack -> onNavigateBack()
                    EmployeeIntent.AddBalance -> addBalance()
                }
            }
        )
    }
}

internal fun NavController.navigateToEmployee() = safeNavigate(EMPLOYEE_ROUTE)