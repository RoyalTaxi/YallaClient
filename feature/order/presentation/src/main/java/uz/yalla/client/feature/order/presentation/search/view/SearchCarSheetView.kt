package uz.yalla.client.feature.order.presentation.search.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.progress.YallaProgressBar
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.state.SheetValue
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.dialog.ConfirmationDialog
import uz.yalla.client.feature.order.presentation.components.dialog.ConfirmationDialogEvent
import uz.yalla.client.feature.order.presentation.components.items.OrderActionsItem
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.view.sheet.OrderDetailsBottomSheet
import uz.yalla.client.feature.order.presentation.search.SEARCH_CAR_ROUTE
import uz.yalla.client.feature.order.presentation.search.model.SearchCarSheetViewModel
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchCarSheet(
    point: MapPoint,
    orderId: Int,
    tariffId: Int,
    viewModel: SearchCarSheetViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState(
        rememberBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            defineValues = {
                SheetValue.PartiallyExpanded at height(state.headerHeight + state.footerHeight + 40.dp)
                SheetValue.Expanded at contentHeight
            }
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var currentTime by rememberSaveable { mutableIntStateOf(0) }
    val cancelOrderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val orderDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val progress by animateFloatAsState(
        targetValue = currentTime.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "progress_anim"
    )

    LaunchedEffect(Unit) {
        SearchCarSheetChannel.register(lifecycleOwner)
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.setPoint(point)
            viewModel.setTariffId(tariffId)
            viewModel.setOrderId(orderId)
        }
    }

    LaunchedEffect(state.setting) {
        launch(Dispatchers.Default) {
            while (currentTime < (state.setting?.orderCancelTime ?: 1)) {
                delay(1.seconds)
                currentTime += 1
            }
        }
    }

    LaunchedEffect(state.footerHeight, state.headerHeight) {
        launch(Dispatchers.Main) {
            scaffoldState.sheetState.refreshValues()
            SheetCoordinator.updateSheetHeight(
                route = SEARCH_CAR_ROUTE,
                height = state.headerHeight + state.footerHeight + 40.dp
            )
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            supervisorScope {
                launch(Dispatchers.IO) {
                    while (isActive && state.setting?.orderCancelTime == null) {
                        viewModel.getSetting()
                        delay(5.seconds)
                    }
                }

                launch(Dispatchers.IO) {
                    while (isActive) {
                        delay(10.seconds)
                        SearchCarSheetChannel.sendIntent(SearchCarSheetIntent.ZoomOut)
                    }
                }
            }
        }
    }

    BackHandler { viewModel.onIntent(SearchCarSheetIntent.AddNewOrder) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = YallaTheme.color.surface,
        content = {},
        sheetContent = {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(
                            color = YallaTheme.color.surface,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .padding(bottom = state.footerHeight + 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onSizeChanged {
                                with(density) {
                                    viewModel.onIntent(
                                        SearchCarSheetIntent.SetHeaderHeight(it.height.toDp())
                                    )
                                }
                            }
                            .background(
                                color = YallaTheme.color.background,
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(20.dp)

                    ) {
                        Text(
                            text = stringResource(R.string.search_cars),
                            color = YallaTheme.color.onBackground,
                            style = YallaTheme.font.title
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(
                                R.string.it_takes_around_x_minute,
                                state.timeout.or0()
                            ),
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.label
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        YallaProgressBar(
                            progress = progress / (state.setting?.orderCancelTime ?: 1),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(YallaTheme.color.background)
                    ) {
                        OrderActionsItem(
                            text = stringResource(R.string.cancel_order),
                            imageVector = Icons.Default.Close,
                            onClick = { viewModel.setCancelBottomSheetVisibility(true) }
                        )

                        OrderActionsItem(
                            text = stringResource(R.string.add_order),
                            imageVector = Icons.Default.Add,
                            onClick = { viewModel.onIntent(SearchCarSheetIntent.AddNewOrder) }
                        )

                        OrderActionsItem(
                            text = stringResource(R.string.order_details),
                            imageVector = Icons.Outlined.Info,
                            onClick = { viewModel.setDetailsBottomSheetVisibility(true) }
                        )
                    }
                }
            }
        }
    )

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {}
                .onSizeChanged {
                    with(density) {
                        viewModel.onIntent(
                            SearchCarSheetIntent.SetFooterHeight(it.height.toDp())
                        )
                    }
                }
                .background(
                    color = YallaTheme.color.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            PrimaryButton(
                text = stringResource(R.string.want_faster),
                enabled = state.isFasterEnabled == true,
                containerColor = YallaTheme.color.primary,
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.orderFaster() }
            )
        }
    }

    if (state.detailsBottomSheetVisibility) {
        state.selectedOrder?.let {
            OrderDetailsBottomSheet(
                order = it,
                sheetState = orderDetailsSheetState,
                onDismissRequest = { viewModel.setDetailsBottomSheetVisibility(false) }
            )
        }
    }

    if (state.cancelBottomSheetVisibility) {
        ConfirmationBottomSheet(
            sheetState = cancelOrderSheetState,
            title = stringResource(R.string.cancel_order_question),
            description = stringResource(R.string.cancel_order_definition),
            actionText = stringResource(R.string.cancel),
            dismissText = stringResource(R.string.wait),
            actionEnabled = state.isOrderCancellable,
            onDismissRequest = {
                viewModel.setCancelBottomSheetVisibility(false)
                scope.launch { cancelOrderSheetState.hide() }
            },
            onConfirm = {
                viewModel.cancelRide()
                viewModel.setCancelBottomSheetVisibility(false)
                scope.launch { cancelOrderSheetState.hide() }
            }
        )
    }

    when (state.dialogEvent) {
        is ConfirmationDialogEvent.Error -> {
            ConfirmationDialog(
                title = stringResource(R.string.error),
                description = stringResource(R.string.error_body),
                dismissText = stringResource(R.string.ok),
                onDismissRequest = { viewModel.dismissDialog() },
            )
        }

        is ConfirmationDialogEvent.Success -> {
            ConfirmationDialog(
                title = stringResource(R.string.success),
                description = stringResource(R.string.success_body),
                dismissText = stringResource(R.string.ok),
                onDismissRequest = { viewModel.dismissDialog() },
            )
        }

        is ConfirmationDialogEvent.Invisible -> {}
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onCleared()
        }
    }
}