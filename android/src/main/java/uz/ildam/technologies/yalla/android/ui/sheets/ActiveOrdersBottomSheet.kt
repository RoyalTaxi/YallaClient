package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.ui.components.item.HistoryOrderItem
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveOrdersBottomSheet(
    sheetState: SheetState,
    orders: List<ShowOrderModel>,
    onSelect: (ShowOrderModel) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        containerColor = YallaTheme.color.white,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                HistoryOrderItem(
                    firstAddress = order.taxi.routes.firstOrNull()?.fullAddress.orEmpty(),
                    secondAddress = order.taxi.routes.lastOrNull()?.fullAddress,
                    time = order.dateTime,
                    totalPrice = order.taxi.totalPrice.toString(),
                    status = order.status.value,
                    onClick = { onSelect(order) }
                )
            }
        }
    }
}