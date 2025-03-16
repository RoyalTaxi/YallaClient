package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel


object MainSheet {
    private val viewModel: MainSheetViewModel by lazy { getKoin().get() }
    internal val mutableIntentFlow = MutableSharedFlow<MainBottomSheetIntent>()
    val intentFlow = mutableIntentFlow.asSharedFlow()

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

        Card(
            colors = CardDefaults.cardColors(YallaTheme.color.gray2),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                OrderTaxiBottomSheet(
                    state = state,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier
                        .graphicsLayer { alpha = 1f - fraction }
                        .zIndex(fraction),
                )

                TariffInfoBottomSheet(
                    state = state,
                    isTariffValidWithOptions = isTariffValidWithOptions,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier
                        .graphicsLayer { alpha = fraction }
                        .zIndex(fraction)
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

    val setDestination: (_: List<Destination>) -> Unit = viewModel::setDestination
}