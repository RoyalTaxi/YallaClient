import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate

const val  SEARCH_FOR_CAR_ROUTE = "search_for_car_bottom_sheet"

fun  NavGraphBuilder.searchForCarBottomSheet(
    sheetHeight: Dp,
    footerHeight: Dp
) {
    composable(SEARCH_FOR_CAR_ROUTE) {

    }
}

fun  NavController.navigateToSearchForCarBottomSheet() : Unit = safeNavigate(SEARCH_FOR_CAR_ROUTE)