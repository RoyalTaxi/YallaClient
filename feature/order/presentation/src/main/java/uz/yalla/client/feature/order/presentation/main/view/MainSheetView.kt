package uz.yalla.client.feature.order.presentation.main.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.search_address.SearchByNameBottomSheet
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.common.sheet.select_from_map.view.SelectFromMapScreen
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.page.OrderTaxiPage
import uz.yalla.client.feature.order.presentation.main.view.page.TariffInfoPage
import uz.yalla.client.feature.order.presentation.main.view.sheet.*
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainSheet(
    viewModel: MainSheetViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val buttonAndOptionsState by viewModel.buttonAndOptionsState.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val prefs = koinInject<AppPreferences>()
    val paymentType by prefs.paymentType.collectAsStateWithLifecycle(PaymentType.CASH)

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

    val paymentMethodSheetState = rememberModalBottomSheetState(true)
    val orderCommentSheetState = rememberModalBottomSheetState(true)
    val searchByNameSheetState = rememberModalBottomSheetState(true)
    val addDestinationSheetState = rememberModalBottomSheetState(true)
    val arrangeDestinationsSheetState = rememberModalBottomSheetState(true)
    val bonusInfoSheetState = rememberModalBottomSheetState(true)

    val fraction by remember {
        derivedStateOf {
            calculateTransitionFraction(scaffoldState.sheetState)
        }
    }

    BackHandler(enabled = state.selectedTariff != null && fraction >= 0.5f) {
        scope.launch {
            scaffoldState.sheetState.animateTo(SheetValue.PartiallyExpanded)
        }
    }

    LaunchedEffect(Unit) {
        MainSheetChannel.register(lifecycleOwner)
    }

    LaunchedEffect(state.selectedTariff?.id, state.location?.point) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch(Dispatchers.IO) {
                state.location?.point.let {
                    while (isActive) {
                        viewModel.updateTimeout()
                        delay(10.seconds)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.getCardList()
            viewModel.setPaymentType(paymentType)
            viewModel.initializeLocation()
        }

        launch {
            viewModel.observeTimeoutAndDrivers()
        }

        launch {
            viewModel.observePaymentMethod()
        }

        launch {
            viewModel.observeChannel()
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.sheetVisibilityListener.collectLatest {
                scaffoldState.sheetState.animateTo(
                    if (scaffoldState.sheetState.targetValue == SheetValue.Expanded) SheetValue.PartiallyExpanded
                    else SheetValue.Expanded
                )
            }
        }
        launch {
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
        sheetContainerColor = YallaTheme.color.surface,
        sheetContent = {
            SheetContent(
                state = state,
                loading = loading,
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
            isBonusEnabled = state.isBonusEnabled,
            sheetState = paymentMethodSheetState,
            paymentTypes = state.cardList,
            selectedPaymentType = state.selectedPaymentType,
            onIntent = viewModel::onIntent
        )
    }

    if (state.isBonusInfoSheetVisibility) {
        BonusInfoBottomSheet(
            sheetState = bonusInfoSheetState,
            isBonusEnabled = state.isBonusEnabled,
            onDismissRequest = {
                scope.launch { bonusInfoSheetState.hide() }
                viewModel.setBonusInfoVisibility(false)
            },
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
            initialAddress = state.location,
            initialDestination = state.destinations.lastOrNull(),
            isForDestination = state.isSearchByNameSheetVisible == SearchByNameSheetValue.FOR_DEST,
            onAddressSelected = { name, lat, lng, addressId ->
                if (addressId != 0) scope.launch(Dispatchers.IO) {
                    MainSheetChannel.sendIntent(
                        OrderTaxiSheetIntent.SetSelectedLocation(
                            location = Location(
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
                    MainSheetChannel.sendIntent(
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
                        MainSheetChannel.sendIntent(
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
        SelectFromMapScreen(
            viewValue = state.selectFromMapViewVisibility,
            startingPoint = when {
                state.destinations.isEmpty() -> state.location?.point
                else -> state.destinations.lastOrNull()?.point
            },
            onSelectLocation = { location ->
                when (state.selectFromMapViewVisibility) {
                    SelectFromMapViewValue.FOR_START -> {
                        scope.launch(Dispatchers.Main) {
                            MainSheetChannel.sendIntent(
                                OrderTaxiSheetIntent.SetSelectedLocation(location)
                            )
                        }
                    }

                    SelectFromMapViewValue.FOR_DEST -> {
                        scope.launch(Dispatchers.Main) {
                            MainSheetChannel.sendIntent(
                                OrderTaxiSheetIntent.SetDestinations(listOf(location.mapToDestination()))
                            )
                        }
                    }

                    else -> {
                        scope.launch(Dispatchers.Main) {
                            MainSheetChannel.sendIntent(
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
                    MainSheetChannel.sendIntent(
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
                    MainSheetChannel.sendIntent(OrderTaxiSheetIntent.SetDestinations(destinations))
                }
            }
        )
    }

    if (state.isSetBonusAmountBottomSheetVisible) {
        SetBonusAmountBottomSheet(
            amount = state.bonusAmount,
            onDismissRequest = viewModel::setBonusAmount
        )
    }

    if (loading) {
        LoadingDialog(modifier = Modifier.zIndex(3f))
    }

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (state.isNotSufficientBalanceDialogVisibility) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = stringResource(R.string.not_sufficient_balance),
            actionText = stringResource(R.string.ok),
            onAction = {
                viewModel.onIntent(MainSheetIntent.FooterIntent.ClickPaymentButton)
                viewModel.setNotSufficientBalanceDialogVisibility(false)
            },
            onDismiss = {
                viewModel.onIntent(MainSheetIntent.FooterIntent.ClickPaymentButton)
                viewModel.setNotSufficientBalanceDialogVisibility(false)
            }
        )
    }
}

@Composable
private fun SheetContent(
    state: MainSheetState,
    fraction: Float,
    loading: Boolean,
    isTariffValidWithOptions: Boolean,
    viewModel: MainSheetViewModel = koinViewModel(),
    onIntent: (MainSheetIntent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        OrderTaxiPage(
            state = state,
            onIntent = onIntent,
            loading = loading,
            onHeightChanged = viewModel::setSheetHeight,
            modifier = Modifier
                .graphicsLayer { alpha = 1f - fraction }
                .zIndex(if (fraction < 0.5f) 1f else 0f)
        )

        if (state.selectedTariff != null && loading.not()) {
            TariffInfoPage(
                state = state,
                isVisible = fraction >= .5,
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

@Composable
private fun Modifier.consumePointerEvents(shouldConsume: Boolean): Modifier {
    return if (shouldConsume) {
        this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {}
        )
    } else this
}

@OptIn(ExperimentalFoundationApi::class)
private fun calculateTransitionFraction(
    sheetState: BottomSheetState<SheetValue>
): Float {
    val partialOffset = sheetState.values.positionOf(SheetValue.PartiallyExpanded)
    val expandedOffset = sheetState.values.positionOf(SheetValue.Expanded)
    val current = sheetState.offset

    if (partialOffset == expandedOffset) return 0f

    val rawFraction = (partialOffset - current) / (partialOffset - expandedOffset)
    return rawFraction.coerceIn(0f, 1f)
}
