package uz.yalla.client.feature.order.presentation.search.model

import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel

data class SearchCarSheetState(
    val loading: Boolean = false,
    val tariffId: Int? = null,
    val orderId: Int? = null,
    val searchingAddressPoint: MapPoint? = null,
    val cancelBottomSheetVisibility: Boolean = false,
    val setting: SettingModel? = null,
    val timeout: Int? = null
)