package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import org.koin.compose.koinInject
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.buttons.ClearOptionsButton
import uz.yalla.client.feature.order.presentation.components.buttons.CreateOrderButton
import uz.yalla.client.feature.order.presentation.components.buttons.LoginButton
import uz.yalla.client.feature.order.presentation.components.buttons.OptionsButton
import uz.yalla.client.feature.order.presentation.components.buttons.PaymentOptionsButton
import uz.yalla.client.feature.order.presentation.components.buttons.SecondaryAddressMandatoryButton
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.FooterIntent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainSheetFooter(
    isTariffValidWithOptions: Boolean,
    sheetState: BottomSheetState<SheetValue>,
    state: MainSheetState,
    onHeightChanged: (Dp) -> Unit,
    onIntent: (FooterIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs = koinInject<AppPreferences>()
    val isDeviceRegistered by prefs.isDeviceRegistered.collectAsState(initial = true)
    val themeType by prefs.themeType.collectAsState(initial = ThemeType.SYSTEM)
    val density = LocalDensity.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .then(
                if (state.isShadowVisible) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            startY = 0f,
                            endY = with(density) { 40.dp.toPx() },
                            tileMode = TileMode.Clamp,
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(.03f),
                                Color.Black.copy(.09f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .onSizeChanged {
                with(density) {
                    it.height.toDp().let { height ->
                        if (height != state.footerHeight) {
                            onHeightChanged(height)
                        }
                    }
                }
            }
            .background(
                color = YallaTheme.color.background,
                shape = if (sheetState.targetValue == SheetValue.Expanded) RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp
                ) else RectangleShape
            )
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        PaymentOptionsButton(
            modifier = Modifier.fillMaxHeight(),
            cardLastNumber = getCardLastNumber(state.selectedPaymentType),
            painter = painterResource(getPaymentIcon(state, themeType)),
            onClick = { onIntent(FooterIntent.ClickPaymentButton) }
        )

        if (!isDeviceRegistered) {
            LoginButton(
                onClick = { onIntent(FooterIntent.Register) }
            )
        } else if (state.isSecondaryAddressMandatory && state.destinations.isEmpty()) {

            SecondaryAddressMandatoryButton()

        } else if (!isTariffValidWithOptions) {

            ClearOptionsButton()

        } else {
            CreateOrderButton(
                onClick = { onIntent(FooterIntent.CreateOrder) }
            )
        }

        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            badgeText = state.getBadgeText(),
            tint = if (isTariffValidWithOptions) YallaTheme.color.primary else YallaTheme.color.red,
            painter = painterResource(
                when {
                    isTariffValidWithOptions.not() -> R.drawable.ic_x
                    sheetState.targetValue == SheetValue.Expanded -> R.drawable.ic_arrow_vertical
                    else -> R.drawable.ic_options
                }
            ),
            onClick = {
                when {
                    isTariffValidWithOptions.not() -> onIntent(FooterIntent.ClearOptions)
                    !state.tariffs?.tariff.isNullOrEmpty() -> onIntent(
                        FooterIntent.ChangeSheetVisibility(
                            sheetState.targetValue != SheetValue.Expanded
                        )
                    )
                }
            }
        )
    }
}

private fun getCardLastNumber(paymentType: PaymentType): String? {
    return when (paymentType) {
        is PaymentType.CARD -> paymentType.cardNumber
            .takeIf { it.length >= 4 }
            ?.takeLast(4)
        else -> null
    }
}

@Composable
private fun getPaymentIcon(state: MainSheetState, themeType: ThemeType): Int {
    val isDarkTheme = when (themeType) {
        ThemeType.LIGHT -> false
        ThemeType.DARK -> true
        else -> isSystemInDarkTheme()
    }

    return getPaymentIconForTheme(state.selectedPaymentType, state.isBonusEnabled, isDarkTheme)
}

private fun getPaymentIconForTheme(
    paymentType: PaymentType,
    isBonusEnabled: Boolean,
    isDarkTheme: Boolean
): Int {
    val cardIconMap = if (isDarkTheme) {
        mapOf(
            16 to (if (isBonusEnabled) R.drawable.ic_humo_bonus else R.drawable.ic_humo),
            32 to (if (isBonusEnabled) R.drawable.ic_uzcard_bonus else R.drawable.ic_uzcard)
        )
    } else {
        mapOf(
            16 to (if (isBonusEnabled) R.drawable.ic_humo_bonus else R.drawable.ic_humo),
            32 to (if (isBonusEnabled) R.drawable.ic_uzcard_bonus else R.drawable.ic_uzcard)
        )
    }

    val defaultIcon = if (isDarkTheme) {
        if (isBonusEnabled) R.drawable.ic_money_bonus else R.drawable.ic_money_color
    } else {
        if (isBonusEnabled) R.drawable.ic_money_bonus else R.drawable.ic_money_color
    }

    return when (paymentType) {
        is PaymentType.CARD -> cardIconMap[paymentType.cardId.length] ?: defaultIcon
        else -> defaultIcon
    }
}