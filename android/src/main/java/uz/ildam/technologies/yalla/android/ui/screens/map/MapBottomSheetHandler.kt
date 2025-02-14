package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.sheets.ActiveOrdersBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.ArrangeDestinationsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OrderCommentBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.PaymentMethodBottomSheet
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.yalla.client.feature.core.map.MapStrategy
import uz.yalla.client.feature.core.sheets.AddDestinationBottomSheet
import uz.yalla.client.feature.core.sheets.ConfirmationBottomSheet
import uz.yalla.client.feature.core.sheets.search_address.SearchByNameBottomSheet
import uz.yalla.client.feature.core.sheets.select_from_map.SelectFromMapBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
class MapBottomSheetHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val searchLocationState: SheetState,
    private val destinationsState: SheetState,
    private val confirmCancellationState: SheetState,
    private val selectPaymentMethodState: SheetState,
    private val orderCommentState: SheetState,
    private val activeOrdersState: SheetState,
    private val addDestinationState: SheetState,
    private val viewModel: MapViewModel
) {
    private var openMapVisibility by mutableStateOf(OpenMapVisibility.INVISIBLE)
    private var searchLocationVisibility by mutableStateOf(SearchLocationVisibility.INVISIBLE)
    private var destinationsVisibility by mutableStateOf(false)
    private var confirmCancellationVisibility by mutableStateOf(false)
    private var selectPaymentMethodVisibility by mutableStateOf(false)
    private var orderCommentVisibility by mutableStateOf(false)
    private var activeOrdersVisibility by mutableStateOf(false)
    private var addDestinationVisibility by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Sheets(
        uiState: MapUIState,
        map: MapStrategy,
        onAddNewCard: () -> Unit,
        onCancel: () -> Unit
    ) {
        AnimatedVisibility(
            visible = searchLocationVisibility != SearchLocationVisibility.INVISIBLE,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (searchLocationVisibility != SearchLocationVisibility.INVISIBLE) SearchByNameBottomSheet(
                initialAddress = uiState.selectedLocation?.name,
                initialDestination = uiState.destinations.getOrNull(0)?.name ?: "",
                sheetState = searchLocationState,
                isForDestination = searchLocationVisibility == SearchLocationVisibility.END,
                onAddressSelected = { name, lat, lng, addressId ->
                    if (searchLocationVisibility == SearchLocationVisibility.START) {
                        if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                            if (addressId != 0) viewModel.getAddressDetails(MapPoint(lat, lng))
                            else {
                                val result =
                                    viewModel.isPointInsidePolygon(MapPoint(lat, lng))
                                if (result.first) {
                                    viewModel.getAddressDetails(MapPoint(lat, lng))
                                    viewModel.setSelectedLocation(addressId = result.second)
                                } else Toast.makeText(context, "Out of service", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else map.animate(MapPoint(lat, lng))
                    } else if (searchLocationVisibility == SearchLocationVisibility.END) {
                        val destinations = uiState.destinations.toMutableList()
                        destinations.add(
                            MapUIState.Destination(
                                name,
                                MapPoint(lat, lng)
                            )
                        )
                        viewModel.setDestinations(destinations)
                    }
                },
                onDestinationSelected = { name, lat, lng, addressId ->
                    val destinations = uiState.destinations.toMutableList()
                    val newDestination = MapUIState.Destination(name, MapPoint(lat, lng))

                    if (destinations.isEmpty()) {
                        destinations.add(newDestination)
                    } else {
                        destinations[0] = newDestination
                    }

                    viewModel.setDestinations(destinations)
                },
                onClickMap = { forDestination ->
                    openMapVisibility =
                        if (!forDestination) {
                            OpenMapVisibility.START
                        } else {
                            OpenMapVisibility.MIDDLE
                        }
                },
                onDismissRequest = {
                    showSearchLocation(false)
                    viewModel.setFoundAddresses(addresses = emptyList())
                },
                deleteDestination = { name ->
                    val updatedDestinations =
                        viewModel.uiState.value.destinations.filterNot { it.name == name }
                    viewModel.setDestinations(updatedDestinations)
                }
            )
        }

        AnimatedVisibility(
            visible = addDestinationVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (addDestinationVisibility) AddDestinationBottomSheet(
                sheetState = addDestinationState,
                onClickMap = { openMapVisibility = OpenMapVisibility.END },
                onDismissRequest = {
                    addDestination(false)
                    viewModel.setFoundAddresses(addresses = emptyList())
                },
                onAddressSelected = { name, lat, lng, addressId ->
                    val destinations = uiState.destinations.toMutableList()
                    destinations.add(
                        MapUIState.Destination(
                            name,
                            MapPoint(lat, lng)
                        )
                    )
                    viewModel.setDestinations(destinations)
                }
            )
        }


        if (openMapVisibility != OpenMapVisibility.INVISIBLE) SelectFromMapBottomSheet(
            startingPoint = when (openMapVisibility) {
                OpenMapVisibility.START -> uiState.selectedLocation?.point
                OpenMapVisibility.MIDDLE -> uiState.destinations.firstOrNull()?.point
                else -> null
            },
            isForDestination = openMapVisibility == OpenMapVisibility.MIDDLE,
            isForNewDestination = openMapVisibility == OpenMapVisibility.END,
            onSelectLocation = { name, lat, lng, isForDestination ->
                if (isForDestination) {
                    if (openMapVisibility == OpenMapVisibility.END) {
                        val destinations = uiState.destinations.toMutableList()
                        destinations.add(
                            MapUIState.Destination(
                                name,
                                MapPoint(lat, lng)
                            )
                        )
                        viewModel.setDestinations(destinations)

                    } else {
                        val destinations = uiState.destinations.toMutableList()
                        val newDestination = MapUIState.Destination(name, MapPoint(lat, lng))

                        if (destinations.isEmpty()) {
                            destinations.add(newDestination)
                        } else {
                            destinations[0] = newDestination
                        }
                        viewModel.setDestinations(destinations)
                    }
                } else {
                    when (uiState.moveCameraButtonState) {
                        MoveCameraButtonState.MyLocationView -> {
                            map.animate(MapPoint(lat = lat, lng = lng))
                        }

                        else -> viewModel.getAddressDetails(MapPoint(lat, lng))
                    }
                }
            },
            onDismissRequest = { openMapVisibility = OpenMapVisibility.INVISIBLE }
        )

        AnimatedVisibility(
            visible = destinationsVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (destinationsVisibility) ArrangeDestinationsBottomSheet(
                destinations = uiState.destinations,
                sheetState = destinationsState,
                onAddNewDestinationClick = {
                    searchLocationVisibility = SearchLocationVisibility.END
                    scope.launch { searchLocationState.show() }
                },
                onDismissRequest = { orderedDestinations ->
                    showDestinations(false)
                    viewModel.setDestinations(orderedDestinations)
                    if (orderedDestinations.isEmpty()) getCurrentLocation(context) { location ->
                        map.animate(MapPoint(location.latitude, location.longitude))
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = confirmCancellationVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (confirmCancellationVisibility) ConfirmationBottomSheet(
                sheetState = confirmCancellationState,
                title = stringResource(R.string.cancel_order),
                description = stringResource(R.string.cancel_order_definition),
                actionText = stringResource(R.string.cancel),
                dismissText = stringResource(R.string.wait),
                onDismissRequest = { showConfirmCancellation(false) },
                onConfirm = {
                    showConfirmCancellation(false)
                    onCancel()
                }
            )
        }

        AnimatedVisibility(
            visible = selectPaymentMethodVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (selectPaymentMethodVisibility) PaymentMethodBottomSheet(
                sheetState = selectPaymentMethodState,
                uiState = uiState,
                onSelectPaymentType = { viewModel.setPaymentType(paymentType = it) },
                onAddNewCard = onAddNewCard,
                onDismissRequest = { showPaymentMethod(false) }
            )
        }

        AnimatedVisibility(
            visible = orderCommentVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (orderCommentVisibility) OrderCommentBottomSheet(
                sheetState = orderCommentState,
                comment = uiState.comment,
                onCommentChange = viewModel::setComment,
                onDismissRequest = { showOrderComment(false) }
            )
        }

        AnimatedVisibility(
            visible = activeOrdersVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (activeOrdersVisibility) ActiveOrdersBottomSheet(
                sheetState = activeOrdersState,
                orders = uiState.orders,
                onDismissRequest = { showActiveOrders(false) },
                onSelect = { order ->
                    viewModel.setSelectedOrder(order)
                    showActiveOrders(false)
                }
            )
        }
    }

    fun showSearchLocation(
        show: Boolean,
        forDest: Boolean = false
    ) {
        if (show) {
            searchLocationVisibility = if (forDest) SearchLocationVisibility.END
            else SearchLocationVisibility.START
            scope.launch { searchLocationState.show() }
        } else {
            searchLocationVisibility = SearchLocationVisibility.INVISIBLE
            scope.launch { searchLocationState.hide() }
        }
    }

    fun showDestinations(
        show: Boolean
    ) {
        destinationsVisibility = show
        scope.launch { if (show) destinationsState.show() else destinationsState.hide() }
    }

    fun addDestination(
        show: Boolean
    ) {
        addDestinationVisibility = show
        scope.launch { if (show) searchLocationState.show() else searchLocationState.hide() }
    }

    fun showConfirmCancellation(
        show: Boolean
    ) {
        confirmCancellationVisibility = show
        scope.launch { if (show) confirmCancellationState.show() else confirmCancellationState.hide() }
    }

    fun showPaymentMethod(show: Boolean) {
        viewModel.getCardList()
        selectPaymentMethodVisibility = show
        scope.launch { if (show) selectPaymentMethodState.show() else selectPaymentMethodState.hide() }
    }

    fun showOrderComment(show: Boolean) {
        orderCommentVisibility = show
        scope.launch { if (show) orderCommentState.show() else orderCommentState.hide() }
    }

    fun showActiveOrders(show: Boolean) {
        activeOrdersVisibility = show
        scope.launch { if (show) activeOrdersState.show() else activeOrdersState.hide() }
    }
}