package uz.yalla.client.feature.history.history_details.intent

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

data class HistoryDetailsState(
    val isMapReady: Boolean,
    val orderDetails: OrderHistoryModel?,
    val routes: List<GetTariffsModel.Map.Routing>,
    val mapUiSettings: MapUiSettings,
    val properties: MapProperties
) {
    companion object {
        val INITIAL = HistoryDetailsState(
            isMapReady = false,
            orderDetails = null,
            routes = emptyList(),
            mapUiSettings = MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                isIndoorEnabled = true,
                isTrafficEnabled = true
            )
        )
    }
}