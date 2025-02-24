package uz.ildam.technologies.yalla.android.ui.sheets

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
    showOptions: (Boolean) -> Unit,
    cleanOptions: () -> Unit
) {
    val isValidTariff = uiState.isTariffValidWithOptions ?: true
    val selectedPayment = uiState.selectedPaymentType
    val selectedTariff = uiState.selectedTariff

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(top = 10.dp)
            .background(YallaTheme.color.white, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            size = if (selectedPayment is PaymentType.CARD) 36.dp else 24.dp,
            painter = painterResource(getPaymentIcon(selectedPayment)),
            onClick = onSelectPaymentMethodClick
        )

        YallaButton(
            text = getButtonText(
                isValidTariff,
                selectedTariff?.secondAddress == true,
                uiState.destinations.isEmpty()
            ),
            enabled = !isLoading && selectedTariff != null && isValidTariff && !(selectedTariff.secondAddress && uiState.destinations.isEmpty()),
            onClick = onCreateOrder,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .height(56.dp)
        )

        OptionsButton(
            modifier = Modifier.fillMaxHeight(),
            badgeText = determineBadgeText(uiState),
            tint = if (isValidTariff) YallaTheme.color.primary else YallaTheme.color.red,
            painter = painterResource(getOptionsIcon(sheetState, isValidTariff)),
            onClick = { handleOptionsClick(isValidTariff, sheetState, uiState, cleanOptions, showOptions) }
        )
    }
}

private fun getPaymentIcon(paymentType: PaymentType?): Int {
    return when (paymentType) {
        is PaymentType.CARD -> when (paymentType.cardId.length) {
            16 -> R.drawable.img_logo_humo
            32 -> R.drawable.img_logo_uzcard
            else -> R.drawable.img_money
        }
        else -> R.drawable.img_money
    }
}

@Composable
private fun getButtonText(isValidTariff: Boolean, selectedTariff: Boolean, hasDestinations: Boolean): String {
    return when {
        !isValidTariff -> stringResource(R.string.options_not_valid)
        selectedTariff && hasDestinations -> stringResource(R.string.required_second_address)
        else -> stringResource(R.string.lets_go)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun getOptionsIcon(sheetState: BottomSheetState<SheetValue>, isValidTariff: Boolean): Int {
    return when {
        !isValidTariff -> R.drawable.ic_x
        sheetState.targetValue == SheetValue.Expanded -> R.drawable.ic_arrow_vertical
        else -> R.drawable.img_options
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun handleOptionsClick(
    isValidTariff: Boolean,
    sheetState: BottomSheetState<SheetValue>,
    uiState: MapUIState,
    cleanOptions: () -> Unit,
    showOptions: (Boolean) -> Unit
) {
    if (!isValidTariff) cleanOptions()
    else if (!uiState.tariffs?.tariff.isNullOrEmpty()) showOptions(sheetState.targetValue != SheetValue.Expanded)
}

private fun determineBadgeText(uiState: MapUIState): String? {
    return when {
        uiState.selectedOptions.isNotEmpty() -> uiState.selectedOptions.size.toString()
        uiState.comment.isNotBlank() -> ""
        else -> null
    }
}