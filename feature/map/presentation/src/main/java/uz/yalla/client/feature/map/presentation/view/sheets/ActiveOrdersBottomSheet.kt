package uz.yalla.client.feature.map.presentation.view.sheets

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
import uz.yalla.client.feature.map.presentation.components.item.ActiveOrderItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveOrdersBottomSheet(
    sheetState: SheetState,
    orders: List<ShowOrderModel>,
    onSelectOrder: (ShowOrderModel) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        containerColor = YallaTheme.color.background,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                ActiveOrderItem(
                    order = order,
                    onClick = {
                        onSelectOrder(order)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}