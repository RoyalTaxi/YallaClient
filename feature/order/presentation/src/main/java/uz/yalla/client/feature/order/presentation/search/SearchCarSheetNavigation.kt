package uz.yalla.client.feature.order.presentation.search

import android.os.Build
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet

const val POINT = "point"
const val ORDER_ID = "order_id"
const val TARIFF_ID = "tariff_id"
const val SEARCH_CAR_ROUTE_BASE = "search_for_car_bottom_sheet"
const val SEARCH_CAR_ROUTE =
    "$SEARCH_CAR_ROUTE_BASE?$POINT={$POINT}&$TARIFF_ID={$TARIFF_ID}&$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.searchForCarBottomSheet() {
    composable(
        route = SEARCH_CAR_ROUTE,
        arguments = listOf(
            navArgument(POINT) { type = NavType.StringType },
            navArgument(ORDER_ID) { type = NavType.IntType },
            navArgument(TARIFF_ID) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            SearchCarSheet.View(
                point = backStackEntry
                    .arguments
                    ?.getParcelable(POINT, MapPoint::class.java)
                    ?: MapPoint.Zero,
                orderId = backStackEntry
                    .arguments
                    ?.getInt(ORDER_ID).or0(),
                tariffId = backStackEntry
                    .arguments
                    ?.getInt(TARIFF_ID).or0()

            )
        else SearchCarSheet.View(
            point = backStackEntry
                .arguments
                ?.getParcelable(POINT) ?: MapPoint.Zero,
            orderId = backStackEntry
                .arguments
                ?.getInt(ORDER_ID).or0(),
            tariffId = backStackEntry
                .arguments
                ?.getInt(TARIFF_ID)
                .or0()
        )
    }
}

fun NavController.navigateToSearchForCarBottomSheet(
    point: MapPoint,
    tariffId: Int,
    orderId: Int
) {
    val route = "$SEARCH_CAR_ROUTE_BASE?$POINT=$point&$TARIFF_ID=$tariffId&$ORDER_ID=$orderId"
    safeNavigate(route)
}