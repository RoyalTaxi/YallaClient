package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import uz.yalla.client.core.analytics.event.Event
import uz.yalla.client.core.analytics.event.Logger
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import kotlin.math.min

private const val CHANGE_AMOUNT = 1000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBonusAmountBottomSheet(
    amount: Long,
    fromBonusInfo: Boolean,
    fromPaymentMethod: Boolean,
    onDismissRequest: (amount: Long) -> Unit
) {
    val prefs = koinInject<AppPreferences>()
    val balance by prefs.balance.collectAsState(0)
    val minBonus by prefs.minBonus.collectAsState(0)
    val maxBonus by prefs.maxBonus.collectAsState(0)
    var currentBonusAmount by remember(minBonus, maxBonus) {
        mutableLongStateOf(amount.coerceIn(minBonus, maxBonus))
    }

    ModalBottomSheet(
        dragHandle = null,
        containerColor = YallaTheme.color.surface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        onDismissRequest = { onDismissRequest(amount) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SetBonusAmountHeader(
                balance = balance,
                minBonus = minBonus,
                maxBonus = maxBonus
            )

            SetBonusAmountBody(
                currentBonusAmount = currentBonusAmount,
                minBonus = minBonus,
                maxBonus = min(maxBonus, balance),
                onDecrement = {
                    currentBonusAmount = (currentBonusAmount - CHANGE_AMOUNT)
                        .coerceAtLeast(minBonus)
                },
                onIncrement = {
                    currentBonusAmount = (currentBonusAmount + CHANGE_AMOUNT)
                        .coerceAtMost(min(maxBonus, balance))
                }
            )

            SetBonusAmountFooter {
                when {
                    fromBonusInfo -> {
                        Logger.log(Event.ActivateBonusClick(Event.OverlayBonusClick))
                    }

                    fromPaymentMethod -> {
                        Logger.log(Event.ActivateBonusClick(Event.PaymentMethodBonusClick))
                    }
                }

                onDismissRequest(currentBonusAmount)
            }
        }
    }
}

@Composable
private fun SetBonusBackground(
    content: @Composable (Modifier) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.background),
        shape = RoundedCornerShape(30.dp)
    ) {
        content(
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SetBonusAmountHeader(
    balance: Long,
    minBonus: Long,
    maxBonus: Long
) {
    SetBonusBackground { modifier ->
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.bonuses, balance.toString()),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.title
            )

            Text(
                text = stringResource(
                    R.string.from_x_to_x,
                    minBonus.toString(),
                    maxBonus.toString()
                ),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.label
            )
        }
    }
}

@Composable
private fun SetBonusAmountBody(
    currentBonusAmount: Long,
    minBonus: Long,
    maxBonus: Long,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    SetBonusBackground { modifier ->
        Row(
            modifier = modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val incrementEnabled = currentBonusAmount < maxBonus
            val decrementEnabled = currentBonusAmount > minBonus

            ChangeBonusButton(
                enabled = decrementEnabled,
                painter = painterResource(R.drawable.ic_minus),
                onClick = onDecrement
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(YallaTheme.color.primary),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.fixed_cost, currentBonusAmount.toString()),
                        color = YallaTheme.color.background,
                        style = YallaTheme.font.labelLarge
                    )
                }
            }

            ChangeBonusButton(
                enabled = incrementEnabled,
                painter = painterResource(R.drawable.ic_plus),
                onClick = onIncrement
            )
        }
    }
}

@Composable
private fun ChangeBonusButton(
    enabled: Boolean,
    painter: Painter,
    onClick: () -> Unit
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = YallaTheme.color.black,
            disabledContainerColor = YallaTheme.color.surface,
            contentColor = YallaTheme.color.onBlack,
            disabledContentColor = YallaTheme.color.onSurface
        )
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SetBonusAmountFooter(
    onClick: () -> Unit
) {
    SetBonusBackground { modifier ->
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            contentPadding = PaddingValues(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(YallaTheme.color.black)
        ) {
            Text(
                text = stringResource(R.string.use),
                color = YallaTheme.color.onBlack,
                style = YallaTheme.font.labelLarge
            )
        }
    }
}