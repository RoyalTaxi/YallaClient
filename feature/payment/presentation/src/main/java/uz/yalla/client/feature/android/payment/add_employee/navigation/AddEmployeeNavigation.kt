package uz.yalla.client.feature.android.payment.add_employee.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.add_employee.model.AddEmployeeRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

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