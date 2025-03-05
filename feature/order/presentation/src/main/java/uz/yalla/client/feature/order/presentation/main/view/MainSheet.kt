package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel


object MainSheet {
    val viewModel: MainSheetViewModel = getKoin().get()
    val intentFlow = viewModel.intentFlow

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        val state by viewModel.uiState.collectAsState()
        val primaryButtonState by viewModel.isButtonEnabled.collectAsState()
        val isTariffValidWithOptions by viewModel.isTariffValidWithOptions.collectAsState()
        val sheetState = rememberBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            defineValues = {
                SheetValue.PartiallyExpanded at height(state.sheetHeight + state.footerHeight)
                SheetValue.Expanded at contentHeight
            }
        )
        val fraction by remember {
            derivedStateOf {
                val partialOffset = sheetState.values.positionOf(SheetValue.PartiallyExpanded)
                val expandedOffset = sheetState.values.positionOf(SheetValue.Expanded)
                val current = sheetState.offset
                if (partialOffset == expandedOffset) 0f
                else {
                    val rawFraction = (partialOffset - current) / (partialOffset - expandedOffset)
                    rawFraction.coerceIn(0f, 1f)
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.setPaymentType(AppPreferences.paymentType)
        }

        Column {
            Box(modifier = Modifier.fillMaxSize()) {
                OrderTaxiBottomSheet(
                    state = state,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier
                        .graphicsLayer { alpha = 1f - fraction }
                        .zIndex(if (fraction < 0.5f) 1f else 0f),
                )

                TariffInfoBottomSheet(
                    state = state,
                    isTariffValidWithOptions = isTariffValidWithOptions,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier
                        .graphicsLayer { alpha = fraction }
                        .zIndex(if (fraction >= 0.5f) 1f else 0f)
                        .then(
                            if (state.selectedTariff != null && state.loading.not()) Modifier.matchParentSize()
                            else Modifier
                        )
                        .pointerInput(fraction) {
                            if (fraction > 0f)
                                awaitPointerEventScope { while (true) awaitPointerEvent() }
                        }
                )
            }

            MainSheetFooter(
                state = state,
                sheetState = sheetState,
                primaryButtonState = primaryButtonState,
                isTariffValidWithOptions = isTariffValidWithOptions,
                onIntent = viewModel::onIntent
            )
        }
    }
}