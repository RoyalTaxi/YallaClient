package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.OptionsButton
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.FooterIntent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainSheetFooter(
    primaryButtonState: Boolean,
    isTariffValidWithOptions: Boolean,
    sheetState: BottomSheetState<SheetValue>,
    state: MainSheetState,
    onIntent: (FooterIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val primaryButtonText = when {
        isTariffValidWithOptions.not() -> stringResource(R.string.options_not_valid)
        state.isSecondaryAddressMandatory && state.destinations.isEmpty() -> stringResource(R.string.required_second_address)
        else -> stringResource(R.string.lets_go)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .then(
                if (state.shadowVisibility) {
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                startY = 0f,
                                endY = with(density) { 20.dp.toPx() },
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
                        if (height != state.footerHeight)
                            onIntent(FooterIntent.SetFooterHeight(height))
                    }
                }
            }
            .padding(top = 10.dp)
            .background(
                YallaTheme.color.white,
                RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .navigationBarsPadding()
            .padding(20.dp)

    ) {
        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            size = if (state.selectedPaymentType is PaymentType.CARD) 36.dp else 24.dp,
            painter = painterResource(
                when (state.selectedPaymentType) {
                    is PaymentType.CARD -> when (state.selectedPaymentType.cardId.length) {
                        16 -> R.drawable.img_logo_humo
                        32 -> R.drawable.img_logo_uzcard
                        else -> R.drawable.img_money
                    }

                    else -> R.drawable.img_money
                }
            ),
            onClick = { onIntent(FooterIntent.ClickPaymentButton) }
        )

        PrimaryButton(
            text = primaryButtonText,
            enabled = primaryButtonState,
            onClick = { onIntent(FooterIntent.CreateOrder) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .height(56.dp)
        )

        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            badgeText = state.getBadgeText(),
            tint = if (isTariffValidWithOptions) YallaTheme.color.primary else YallaTheme.color.red,
            painter = painterResource(
                when {
                    isTariffValidWithOptions.not() -> R.drawable.ic_x
                    sheetState.targetValue == SheetValue.Expanded -> R.drawable.ic_arrow_vertical
                    else -> R.drawable.img_options
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