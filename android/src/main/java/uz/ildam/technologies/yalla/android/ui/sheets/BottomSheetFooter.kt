package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.OptionsButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.sheets.SheetValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheetFooter(
    isLoading: Boolean,
    sheetState: BottomSheetState<SheetValue>,
    uiState: MapUIState,
    modifier: Modifier = Modifier,
    onSelectPaymentMethodClick: () -> Unit,
    onCreateOrder: () -> Unit,
    showOptions: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(top = 10.dp)
            .background(
                color = YallaTheme.color.white,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            size = when (uiState.selectedPaymentType) {
                is PaymentType.CARD -> 36.dp
                else -> 24.dp
            },
            painter = when (uiState.selectedPaymentType) {
                is PaymentType.CARD -> painterResource(
                    when (uiState.selectedPaymentType.cardId.length) {
                        16 -> R.drawable.img_logo_humo
                        32 -> R.drawable.img_logo_uzcard
                        else -> R.drawable.img_money
                    }
                )

                else -> painterResource(R.drawable.img_money)
            },
            onClick = onSelectPaymentMethodClick
        )

        YallaButton(
            text = stringResource(R.string.lets_go),
            enabled = isLoading.not() && uiState.selectedTariff != null,
            contentPadding = PaddingValues(vertical = 20.dp),
            onClick = onCreateOrder,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            badgeText = determineBadgeText(uiState),
            tint = YallaTheme.color.primary,
            painter = when (sheetState.targetValue) {
                SheetValue.Expanded -> painterResource(R.drawable.ic_arrow_vertical)
                SheetValue.PartiallyExpanded -> painterResource(R.drawable.img_options)
            },
            onClick = {
                if (uiState.tariffs?.tariff?.isNotEmpty() == true)
                    showOptions(sheetState.targetValue != SheetValue.Expanded)
            }
        )
    }
}

private fun determineBadgeText(uiState: MapUIState): String? {
    return when {
        uiState.selectedOptions.isNotEmpty() -> uiState.selectedOptions.size.toString()
        uiState.comment.isNotBlank() -> ""
        else -> null
    }
}