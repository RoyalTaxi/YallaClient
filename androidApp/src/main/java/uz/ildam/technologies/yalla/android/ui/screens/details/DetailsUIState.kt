package uz.ildam.technologies.yalla.android.ui.screens.details

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

data class DetailsUIState(
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