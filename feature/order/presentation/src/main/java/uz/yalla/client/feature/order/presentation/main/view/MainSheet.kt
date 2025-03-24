package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel
import uz.yalla.client.feature.order.presentation.main.view.page.OrderTaxiPage
import uz.yalla.client.feature.order.presentation.main.view.page.TariffInfoPage
import uz.yalla.client.feature.order.presentation.main.view.sheet.PaymentMethodBottomSheet

object MainSheet {
    private val viewModel: MainSheetViewModel by lazy { getKoin().get() }
    private val _intentFlow = MutableSharedFlow<MainBottomSheetIntent>(1)
    val intentFlow = _intentFlow.asSharedFlow()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        val state by viewModel.uiState.collectAsState()
        val buttonAndOptionsState by viewModel.buttonAndOptionsState.collectAsState()

        val partialSheetHeight = state.sheetHeight + state.footerHeight

        val scaffoldState = rememberBottomSheetScaffoldState(
            rememberBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
                defineValues = {
                    SheetValue.PartiallyExpanded at height(partialSheetHeight)
                    SheetValue.Expanded at
                            if (state.selectedTariff != null) contentHeight
                            else height(partialSheetHeight)
                }
            )
        )

        val paymentMethodSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val fraction by remember {
            derivedStateOf {
                calculateTransitionFraction(scaffoldState.sheetState)
            }
        }

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                viewModel.getPolygon()
                viewModel.getCardList()
                viewModel.setPaymentType(AppPreferences.paymentType)
            }
            launch(Dispatchers.Main) {
                viewModel.sheetVisibilityListener.collectLatest {
                    scaffoldState.sheetState.animateTo(
                        if (scaffoldState.sheetState.targetValue == SheetValue.Expanded) SheetValue.PartiallyExpanded
                        else SheetValue.Expanded
                    )
                }
            }
        }

        LaunchedEffect(state.isPaymentMethodSheetVisible) {
            if (state.isPaymentMethodSheetVisible) {
                paymentMethodSheetState.show()
            } else if (paymentMethodSheetState.isVisible) {
                paymentMethodSheetState.hide()
            }
        }

        LaunchedEffect(paymentMethodSheetState.isVisible) {
            if (!paymentMethodSheetState.isVisible && state.isPaymentMethodSheetVisible) {
                viewModel.onIntent(MainBottomSheetIntent.PaymentMethodSheetIntent.OnDismissRequest)
            }
        }

        LaunchedEffect(state.selectedTariff) {
            launch(Dispatchers.Main) {
                scaffoldState.sheetState.refreshValues()
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetDragHandle = null,
            content = { },
            sheetShape = when (scaffoldState.sheetState.targetValue) {
                SheetValue.PartiallyExpanded -> RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                SheetValue.Expanded -> RectangleShape
            },
            sheetContainerColor = YallaTheme.color.gray2,
            sheetContent = {
                SheetContent(
                    state = state,
                    fraction = fraction,
                    isTariffValidWithOptions = buttonAndOptionsState.isTariffValidWithOptions,
                    onIntent = viewModel::onIntent
                )
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            MainSheetFooter(
                state = state,
                sheetState = scaffoldState.sheetState,
                primaryButtonState = buttonAndOptionsState.isButtonEnabled,
                isTariffValidWithOptions = buttonAndOptionsState.isTariffValidWithOptions,
                onIntent = viewModel::onIntent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(2f)
            )
        }

        if (state.isPaymentMethodSheetVisible) {
            PaymentMethodBottomSheet(
                sheetState = paymentMethodSheetState,
                paymentTypes = state.cardList,
                selectedPaymentType = state.selectedPaymentType,
                onIntent = viewModel::onIntent
            )
        }
    }

    @Composable
    private fun SheetContent(
        state: MainSheetState,
        fraction: Float,
        isTariffValidWithOptions: Boolean,
        onIntent: (MainBottomSheetIntent) -> Unit
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            OrderTaxiPage(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .graphicsLayer { alpha = 1f - fraction }
                    .zIndex(if (fraction < 0.5f) 1f else 0f)
            )

            if (state.selectedTariff != null && !state.loading) {
                TariffInfoPage(
                    state = state,
                    isTariffValidWithOptions = isTariffValidWithOptions,
                    onIntent = onIntent,
                    modifier = Modifier
                        .graphicsLayer { alpha = fraction }
                        .zIndex(if (fraction >= 0.5f) 1f else 0f)
                        .consumePointerEvents(fraction > 0f)
                )
            }
        }
    }

    private fun Modifier.consumePointerEvents(shouldConsume: Boolean): Modifier {
        return if (shouldConsume) {
            this.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
        } else {
            this
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun calculateTransitionFraction(sheetState: BottomSheetState<SheetValue>): Float {
        val partialOffset = sheetState.values.positionOf(SheetValue.PartiallyExpanded)
        val expandedOffset = sheetState.values.positionOf(SheetValue.Expanded)
        val current = sheetState.offset

        if (partialOffset == expandedOffset) return 0f

        val rawFraction = (partialOffset - current) / (partialOffset - expandedOffset)
        return rawFraction.coerceIn(0f, 1f)
    }

    // Public interface for ViewModel
    val setDestination: (List<Destination>) -> Unit = viewModel::setDestination
    val setLocation: (SelectedLocation) -> Unit = viewModel::setSelectedLocation
    val setLoading: (Boolean) -> Unit = viewModel::setLoading

    // Used by ViewModel to emit intents
    internal val mutableIntentFlow: MutableSharedFlow<MainBottomSheetIntent> get() = _intentFlow
}