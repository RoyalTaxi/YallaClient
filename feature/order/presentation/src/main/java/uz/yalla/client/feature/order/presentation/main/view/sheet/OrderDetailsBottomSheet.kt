package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.item.LocationItem
import uz.yalla.client.core.common.item.OrderDetailBonusItem
import uz.yalla.client.core.common.item.OrderDetailItem
import uz.yalla.client.core.common.item.formatWithSpaces
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.buttons.ProvideDescriptionButton
import uz.yalla.client.feature.order.presentation.components.items.OptionsItem
import uz.yalla.client.feature.order.presentation.components.items.OrderActionsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsBottomSheet(
    order: ShowOrderModel,
    sheetState: SheetState,
    onCancelOrder: () -> Unit,
    onAddNewOrder: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.surface,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.surface)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp)
                    )
                    .padding(20.dp)
            ) {
                if (order.taxi.routes.isNotEmpty()) {
                    LocationItem(
                        location = order.taxi.routes.first().fullAddress,
                        isFirst = true,
                        isLast = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (order.taxi.routes.size > 2) {
                    order.taxi.routes.drop(1).dropLast(1).forEach {
                        LocationItem(
                            location = it.fullAddress,
                            isFirst = false,
                            isLast = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

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
                    .clip(RoundedCornerShape(30.dp))
                    .background(YallaTheme.color.background)
                    .padding(vertical = 10.dp)
            ) {
                OrderDetailItem(
                    title = stringResource(R.string.tariff),
                    description = order.taxi.tariff
                )

                OrderDetailItem(
                    title = stringResource(id = R.string.payment),
                    bodyText = when (order.paymentType) {
                        is PaymentType.CARD -> stringResource(R.string.with_card)
                        else -> stringResource(R.string.cash)
                    },
                    description = order.taxi.totalPrice.takeIf { it != 0 }?.let {
                        stringResource(R.string.fixed_cost, it)
                    }
                )

                order.taxi.bonusAmount.takeIf { it != 0 }?.let {
                    OrderDetailBonusItem(
                        title = stringResource(R.string.bonus),
                        bonus = stringResource(R.string.added_bonus, it.formatWithSpaces())
                    )
                }

                if (order.status !in OrderStatus.nonInteractive) {
                    if (order.executor.givenNames.isNotEmpty()) {
                        OrderDetailItem(
                            title = stringResource(R.string.driver),
                            description = order.executor.givenNames
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
                }

                if (order.comment.isNotEmpty()) {
                    ProvideDescriptionButton(
                        title = stringResource(R.string.comment_to_driver),
                        description = order.comment,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = YallaTheme.color.gray
                            )
                        },
                        onClick = {}
                    )
                }

                if (order.taxi.services.isNotEmpty()) Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(YallaTheme.color.background)
                ) {
                    order.taxi.services.forEach { service ->
                        OptionsItem(
                            option = service,
                            isSelected = true,
                            enabled = false,
                            onChecked = {}
                        )
                    }
                }

                OrderActionsItem(
                    text = stringResource(R.string.add_order),
                    imageVector = Icons.Default.Add,
                    onClick = onAddNewOrder
                )

                if (order.status != OrderStatus.InFetters) {
                    OrderActionsItem(
                        text = stringResource(R.string.cancel_order),
                        imageVector = Icons.Default.Close,
                        tintColor = YallaTheme.color.red,
                        contentColor = YallaTheme.color.red,
                        onClick = onCancelOrder
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = YallaTheme.color.background,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
            ) {
                PrimaryButton(
                    text = stringResource(R.string.close),
                    onClick = onDismissRequest,
                    containerColor = YallaTheme.color.surface,
                    contentColor = YallaTheme.color.onBackground,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
