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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.getKoin
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.search_address.SearchByNameBottomSheet
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapView
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.page.OrderTaxiPage
import uz.yalla.client.feature.order.presentation.main.view.page.TariffInfoPage
import uz.yalla.client.feature.order.presentation.main.view.sheet.ArrangeDestinationsBottomSheet
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderCommentBottomSheet
import uz.yalla.client.feature.order.presentation.main.view.sheet.PaymentMethodBottomSheet

object MainSheet {
    private val viewModel: MainSheetViewModel by lazy { getKoin().get() }
    private val _intentFlow = MutableSharedFlow<MainSheetIntent>()
    val intentFlow = _intentFlow.asSharedFlow()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun View() {
        val state by viewModel.uiState.collectAsState()
        val buttonAndOptionsState by viewModel.buttonAndOptionsState.collectAsState()
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()
        val prefs = koinInject<AppPreferences>()
        val paymentType by prefs.paymentType.collectAsState(initial = PaymentType.CASH)

        val scaffoldState = rememberBottomSheetScaffoldState(
            rememberBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
                defineValues = {
                    val partialSheetHeight = state.sheetHeight + state.footerHeight
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

        val orderCommentSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val searchByNameSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val addDestinationSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val arrangeDestinationsSheetState = rememberModalBottomSheetState(
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
                viewModel.setPaymentType(paymentType)
            }
            launch(Dispatchers.Main) {
                viewModel.sheetVisibilityListener.collectLatest {
                    scaffoldState.sheetState.animateTo(
                        if (scaffoldState.sheetState.targetValue == SheetValue.Expanded) SheetValue.PartiallyExpanded
                        else SheetValue.Expanded
                    )
                }
            }

            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                SheetCoordinator.updateSheetHeight(
                    route = MAIN_SHEET_ROUTE,
                    height = state.sheetHeight + state.footerHeight
                )
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
                viewModel.onIntent(PaymentMethodSheetIntent.OnDismissRequest)
            }
        }

        LaunchedEffect(state.sheetHeight) {
            launch(Dispatchers.Main) {
                scaffoldState.sheetState.refreshValues()
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
                onHeightChanged = viewModel::setFooterHeight,
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

        if (state.isOrderCommentSheetVisible) {
            OrderCommentBottomSheet(
                sheetState = orderCommentSheetState,
                comment = state.comment,
                onIntent = viewModel::onIntent
            )
        }

        if (state.isSearchByNameSheetVisible != SearchByNameSheetValue.INVISIBLE) {
            SearchByNameBottomSheet(
                sheetState = searchByNameSheetState,
                initialAddress = state.selectedLocation,
                initialDestination = state.destinations.lastOrNull(),
                isForDestination = state.isSearchByNameSheetVisible == SearchByNameSheetValue.FOR_DEST,
                onAddressSelected = { name, lat, lng, addressId ->
                    scope.launch(Dispatchers.IO) {
                        mutableIntentFlow.emit(
                            OrderTaxiSheetIntent.SetSelectedLocation(
                                selectedLocation = SelectedLocation(
                                    name = name,
                                    point = MapPoint(lat, lng),
                                    addressId = addressId
                                )
                            )
                        )
                    }
                },
                onDestinationSelected = { name, lat, lng, _ ->
                    scope.launch(Dispatchers.IO) {
                        mutableIntentFlow.emit(
                            OrderTaxiSheetIntent.SetDestinations(
                                destinations = listOf(
                                    Destination(
                                        name = name,
                                        point = MapPoint(lat, lng)
                                    )
                                )
                            )
                        )
                    }
                },
                onDismissRequest = {
                    viewModel.setSearchByNameSheetVisibility(SearchByNameSheetValue.INVISIBLE)
                },
                onClickMap = { forDestination ->
                    viewModel.setSelectFromMapViewVisibility(
                        if (forDestination) SelectFromMapViewValue.FOR_DEST
                        else SelectFromMapViewValue.FOR_START
                    )
                },
                deleteDestination = {
                    val currentDestinations = state.destinations
                    if (currentDestinations.isNotEmpty()) {
                        val updatedDestinations = currentDestinations.drop(1)
                        scope.launch(Dispatchers.IO) {
                            mutableIntentFlow.emit(
                                OrderTaxiSheetIntent.SetDestinations(
                                    destinations = updatedDestinations
                                )
                            )
                        }
                    }
                }
            )
        }

        if (state.selectFromMapViewVisibility != SelectFromMapViewValue.INVISIBLE) {
            SelectFromMapView(
                viewValue = state.selectFromMapViewVisibility,
                startingPoint = when {
                    state.destinations.isEmpty() -> state.selectedLocation?.point
                    else -> state.destinations.lastOrNull()?.point
                },
                onSelectLocation = { location ->
                    when (state.selectFromMapViewVisibility) {
                        SelectFromMapViewValue.FOR_START -> {
                            scope.launch(Dispatchers.Main) {
                                mutableIntentFlow.emit(
                                    OrderTaxiSheetIntent.SetSelectedLocation(location)
                                )
                            }
                        }

                        SelectFromMapViewValue.FOR_DEST -> {
                            scope.launch(Dispatchers.Main) {
                                mutableIntentFlow.emit(
                                    OrderTaxiSheetIntent.SetDestinations(listOf(location.mapToDestination()))
                                )
                            }
                        }

                        else -> {
                            scope.launch(Dispatchers.Main) {
                                mutableIntentFlow.emit(
                                    OrderTaxiSheetIntent.AddDestination(
                                        destination = location.mapToDestination()
                                    )
                                )
                            }
                        }
                    }
                },
                onDismissRequest = {
                    viewModel.setSelectFromMapViewVisibility(SelectFromMapViewValue.INVISIBLE)
                }
            )
        }

        if (state.isAddDestinationSheetVisible) {
            AddDestinationBottomSheet(
                sheetState = addDestinationSheetState,
                nearbyAddress = state.destinations.lastOrNull(),
                onClickMap = {
                    viewModel.setSelectFromMapViewVisibility(SelectFromMapViewValue.FOR_NEW_DEST)
                },
                onDismissRequest = {
                    viewModel.setAddDestinationSheetVisibility(false)
                    scope.launch { addDestinationSheetState.hide() }
                },
                onAddressSelected = { location ->
                    scope.launch(Dispatchers.Main) {
                        mutableIntentFlow.emit(
                            OrderTaxiSheetIntent.AddDestination(
                                destination = location.mapToDestination()
                            )
                        )
                    }
                }
            )
        }

        if (state.isArrangeDestinationsSheetVisible) {
            ArrangeDestinationsBottomSheet(
                sheetState = arrangeDestinationsSheetState,
                destinations = state.destinations,
                onAddNewDestinationClick = {
                    viewModel.setArrangeDestinationsSheetVisibility(false)
                    viewModel.setAddDestinationSheetVisibility(true)
                },
                onDismissRequest = { destinations ->
                    viewModel.setArrangeDestinationsSheetVisibility(false)
                    scope.launch(Dispatchers.Main.immediate) {
                        mutableIntentFlow.emit(OrderTaxiSheetIntent.SetDestinations(destinations))
                    }
                }
            )
        }

        if (state.loading) LoadingDialog()
    }

    @Composable
    private fun SheetContent(
        state: MainSheetState,
        fraction: Float,
        isTariffValidWithOptions: Boolean,
        onIntent: (MainSheetIntent) -> Unit
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            OrderTaxiPage(
                state = state,
                onIntent = onIntent,
                onHeightChanged = viewModel::setSheetHeight,
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

    val setDestination: (List<Destination>) -> Unit = viewModel::setDestination
    val setLocation: (SelectedLocation) -> Unit = viewModel::setSelectedLocation

    internal val mutableIntentFlow: MutableSharedFlow<MainSheetIntent> get() = _intentFlow
}