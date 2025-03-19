package uz.yalla.client.feature.history.history_details.model

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.yalla.client.feature.domain.model.OrderHistoryModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

internal data class HistoryDetailsUIState(
    val orderDetails: OrderHistoryModel? = null,
    val routes: List<GetTariffsModel.Map.Routing> = emptyList(),
    val mapUiSettings: MapUiSettings = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false
    ),
    val properties: MapProperties = MapProperties(
        mapType = MapType.NORMAL,
        isBuildingEnabled = true,
        isIndoorEnabled = true,
        isTrafficEnabled = true
    )
)