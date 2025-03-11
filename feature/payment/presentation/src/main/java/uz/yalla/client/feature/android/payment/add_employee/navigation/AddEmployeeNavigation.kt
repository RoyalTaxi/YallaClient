package uz.yalla.client.feature.android.payment.add_employee.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.payment.add_employee.model.AddEmployeeRoute

internal const val ADD_EMPLOYEE_ROUTE = "add_employee_route"

internal fun NavGraphBuilder.addEmployeeScreen(
    onNavigateBack: () -> Unit
) {

    composable(
        route = ADD_EMPLOYEE_ROUTE
    ) {

        AddEmployeeRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

internal fun NavController.navigateToAddEmployee() = safeNavigate(ADD_EMPLOYEE_ROUTE)