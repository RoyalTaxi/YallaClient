package uz.yalla.client.core.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.OrderStatus

@Composable
fun getOrderStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.New -> stringResource(R.string.sending)
        OrderStatus.Sending -> stringResource(R.string.sending)
        OrderStatus.UserSending -> stringResource(R.string.user_sending)
        OrderStatus.NonStopSending -> stringResource(R.string.non_stop_sending)
        OrderStatus.AtAddress -> stringResource(R.string.at_address)
        OrderStatus.InFetters -> stringResource(R.string.in_fetters)
        OrderStatus.Appointed -> stringResource(R.string.appointed)
        OrderStatus.Completed -> stringResource(R.string.completed)
        OrderStatus.Canceled -> stringResource(R.string.canceled)
    }
}