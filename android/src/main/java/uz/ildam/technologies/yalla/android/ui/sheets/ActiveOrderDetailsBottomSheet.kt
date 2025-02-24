package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.item.LocationItem
import uz.ildam.technologies.yalla.android.ui.components.item.OrderDetailItem
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun ActiveOrderDetailsBottomSheet(
    uiState: MapUIState,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(YallaTheme.color.white)
            .padding(20.dp)
    ) {
        LocationItem(
            location = uiState.selectedLocation?.name ?: "",
            isFirst = true,
            isLast = false,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.destinations.isNotEmpty()) {
            for (index in 1 until uiState.destinations.size - 1) {
                uiState.destinations[index].name?.let {
                    LocationItem(
                        location = it,
                        isFirst = false,
                        isLast = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            LocationItem(
                location = null,
                isFirst = false,
                isLast = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OrderDetailItem(
            title = stringResource(R.string.status),
            descriptor = uiState.selectedTariff?.name
        )

        uiState.selectedOrder?.let {
            OrderDetailItem(
                title = stringResource(R.string.payment),
                bodyText = uiState.selectedPaymentType.typeName,
                descriptor = stringResource(
                    R.string.fixed_cost,
                    it.taxi.totalPrice
                ),
            )

            OrderDetailItem(
                title = stringResource(R.string.driver),
                descriptor = it.executor.givenNames
            )

            OrderDetailItem(
                title = stringResource(R.string.car),
                bodyText = "${it.executor.driver.mark} ${it.executor.driver.model}",
                carNumber = it.executor.driver.stateNumber
            )
        }

        Box(
            modifier = Modifier.background(
                color = YallaTheme.color.white,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
        ) {
            YButton(
                text = stringResource(R.string.close),
                onClick = onClose,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
        }
    }
}