package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.item.LocationItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.OptionsItem
import uz.yalla.client.feature.order.presentation.components.OrderDetailItem
import uz.yalla.client.feature.order.presentation.components.ProvideDescriptionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsBottomSheet(
    order: ShowOrderModel,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.gray2)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp)
                    )
                    .padding(20.dp)
            ) {
                LocationItem(
                    location = order.taxi.routes.first().fullAddress,
                    isFirst = true,
                    isLast = false,
                    modifier = Modifier.fillMaxWidth()
                )


                if (order.taxi.routes.size > 1) {
                    LocationItem(
                        location = order.taxi.routes.last().fullAddress,
                        isFirst = false,
                        isLast = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(vertical = 20.dp)
            ) {
                OrderDetailItem(
                    title = stringResource(R.string.status),
                    descriptor = order.taxi.tariff
                )


                OrderDetailItem(
                    title = stringResource(id = R.string.payment),
                    bodyText = order.paymentType,
                    descriptor = stringResource(
                        R.string.fixed_cost,
                        order.taxi.totalPrice
                    ),
                )


                if (order.comment.isNotEmpty()) {
                    ProvideDescriptionButton(
                        title = stringResource(R.string.comment_to_driver),
                        description = order.comment,
                        onClick = {}
                    )
                }

                if (order.executor.givenNames.isNotEmpty()) {
                    OrderDetailItem(
                        title = stringResource(R.string.driver),
                        descriptor = order.executor.givenNames
                    )
                }


                order.executor.driver.let {
                    if (it.mark.isNotBlank() && it.stateNumber.isNotBlank() && it.model.isNotBlank())
                        OrderDetailItem(
                            title = stringResource(R.string.car),
                            bodyText = "${it.mark} ${it.model}",
                            carNumber = it.stateNumber
                        )
                }

                order.taxi.services.forEach { service ->
                    service.name?.let {
                        OptionsItem(
                            option = it,
                            isSelected = true,
                            onChecked = {}
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
            ) {
                PrimaryButton(
                    text = stringResource(R.string.close),
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}