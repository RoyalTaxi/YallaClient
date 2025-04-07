package uz.yalla.client.feature.order.presentation.search.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class SearchCarSheetState(
    val loading: Boolean = false,
    val tariffId: Int? = null,
    val orderId: Int? = null,
    val searchingAddressPoint: MapPoint? = null,
    val selectedOrder: ShowOrderModel? = null,
    val detailsBottomSheetVisibility: Boolean = false,
    val cancelBottomSheetVisibility: Boolean = false,
    val setting: SettingModel? = null,
    val timeout: Int? = null,
    val isFasterEnabled: Boolean? = null,
    val headerHeight: Dp = 0.dp,
    val footerHeight: Dp = 0.dp,
)