package uz.yalla.client.core.common.maps.model

import android.content.Context
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.GoogleMap
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
sealed interface MapsIntent {
    data class OnMapReady(val googleMap: GoogleMap) : MapsIntent
    data class MoveTo(val point: MapPoint) : MapsIntent
    data class AnimateTo(val point: MapPoint) : MapsIntent
    data class MoveToMyLocation(val context: Context) : MapsIntent
    data class AnimateToMyLocation(val context: Context) : MapsIntent
    data object FitBounds : MapsIntent
    data object AnimateFitBounds : MapsIntent
    data object ZoomOut : MapsIntent
    data class SetGoogleMarkPadding(val paddingPx: Int) : MapsIntent
    data class SetBottomPadding(val padding: Dp) : MapsIntent
    data class SetTopPadding(val padding: Dp) : MapsIntent
    data class SetMapPadding(val paddingPx: Int) : MapsIntent
    data class UpdateRoute(val route: List<MapPoint>) : MapsIntent
    data class UpdateLocations(val locations: List<MapPoint>) : MapsIntent
    data class UpdateCarArrivesInMinutes(val minutes: Int?) : MapsIntent
    data class UpdateOrderEndsInMinutes(val minutes: Int?) : MapsIntent
    data class UpdateOrderStatus(val status: ShowOrderModel?) : MapsIntent
    data class UpdateDriver(val driver: Executor?) : MapsIntent
    data class UpdateDrivers(val drivers: List<Executor>) : MapsIntent
}
