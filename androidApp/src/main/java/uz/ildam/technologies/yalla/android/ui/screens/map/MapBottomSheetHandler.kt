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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.ui.sheets.ArrangeDestinationsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.ConfirmCancellationBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.PaymentMethodBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SetOrderOptionsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheet
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.core.data.mapper.or0

@OptIn(ExperimentalMaterial3Api::class)
class MapBottomSheetHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val searchLocationState: SheetState,
    private val destinationsState: SheetState,
    private val tariffState: SheetState,
    private val optionsState: SheetState,
    private val confirmCancellationState: SheetState,
    private val selectPaymentMethodState: SheetState,
    private val viewModel: MapViewModel
) {
    private var openMapVisibility by mutableStateOf(OpenMapVisibility.INVISIBLE)
    private var searchLocationVisibility by mutableStateOf(SearchLocationVisibility.INVISIBLE)
    private var destinationsVisibility by mutableStateOf(false)
    private var tariffVisibility by mutableStateOf(false)
    private var optionsVisibility by mutableStateOf(false)
    private var confirmCancellationVisibility by mutableStateOf(false)
    private var selectPaymentMethodVisibility by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Sheets(
        uiState: MapUIState,
        currentLatLng: MutableState<MapPoint>,
        actionHandler: MapActionHandler,
        onAddNewCard: () -> Unit,
        onCancel: () -> Unit
    ) {
        AnimatedVisibility(
            visible = searchLocationVisibility != SearchLocationVisibility.INVISIBLE,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (searchLocationVisibility != SearchLocationVisibility.INVISIBLE) SearchByNameBottomSheet(
                sheetState = searchLocationState,
                foundAddresses = uiState.foundAddresses,
                isForDestination = searchLocationVisibility == SearchLocationVisibility.END,
                onAddressSelected = { dest ->
                    if (searchLocationVisibility == SearchLocationVisibility.START) {
                        if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                            if (dest.addressId != 0) viewModel.getAddressDetails(
                                MapPoint(dest.lat, dest.lng)
                            )
                            else {
                                val result =
                                    viewModel.isPointInsidePolygon(MapPoint(dest.lat, dest.lng))
                                if (result.first) {
                                    viewModel.getAddressDetails(MapPoint(dest.lat, dest.lng))
                                    viewModel.setSelectedLocation(addressId = result.second)
                                } else Toast.makeText(context, "Out of service", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            currentLatLng.value = MapPoint(dest.lat, dest.lng)
                            actionHandler.moveCamera(
                                MapPoint(
                                    lat = dest.lat,
                                    lng = dest.lng
                                ),
                                animate = true
                            )
                        }
                    } else if (searchLocationVisibility == SearchLocationVisibility.END) {
                        val destinations = uiState.destinations.toMutableList()
                        destinations.add(
                            MapUIState.Destination(
                                dest.name,
                                MapPoint(dest.lat, dest.lng)
                            )
                        )
                        viewModel.setDestinations(destinations)
                    }
                },
                onSearchForAddress = {
                    viewModel.searchForAddress(
                        query = it,
                        point = currentLatLng.value
                    )
                },
                onClickMap = {
                    openMapVisibility =
                        if (searchLocationVisibility == SearchLocationVisibility.START) {
                            OpenMapVisibility.START
                        } else {
                            OpenMapVisibility.END
                        }
                },
                onDismissRequest = {
                    showSearchLocation(false)
                    viewModel.setFoundAddresses(addresses = emptyList())
                }
            )
        }


        if (openMapVisibility != OpenMapVisibility.INVISIBLE) SelectFromMapBottomSheet(
            isForDestination = openMapVisibility == OpenMapVisibility.END,
            onSelectLocation = { name, location, isForDestination ->
                if (isForDestination) {
                    val destinations = uiState.destinations.toMutableList()
                    destinations.add(
                        MapUIState.Destination(
                            name,
                            MapPoint(location.latitude, location.longitude)
                        )
                    )
                    viewModel.setDestinations(destinations)
                } else {
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                        viewModel.getAddressDetails(MapPoint(location.latitude, location.longitude))
                    } else {
                        actionHandler.moveCamera(
                            MapPoint(
                                lat = location.latitude,
                                lng = location.longitude
                            ),
                            animate = true
                        )
                        currentLatLng.value = MapPoint(location.latitude, location.longitude)
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
                        currentLatLng.value = MapPoint(location.latitude, location.longitude)
                        actionHandler.moveCamera(
                            mapPoint = MapPoint(location.latitude, location.longitude),
                            animate = true
                        )
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = tariffVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedTariff?.let { selectedTariff ->
                if (tariffVisibility) TariffInfoBottomSheet(
                    sheetState = tariffState,
                    tariff = selectedTariff,
                    arrivingTime = uiState.timeout.or0(),
                    onDismissRequest = { showTariff(false) }
                )
            }
        }

        AnimatedVisibility(
            visible = optionsVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedTariff?.let { selectedTariff ->
                if (optionsVisibility) SetOrderOptionsBottomSheet(
                    sheetState = optionsState,
                    selectedTariff = selectedTariff,
                    options = uiState.options,
                    selectedOptions = uiState.selectedOptions,
                    onSave = { options -> viewModel.setSelectedOptions(options) },
                    onDismissRequest = { showOptions(false) }
                )
            }
        }

        AnimatedVisibility(
            visible = confirmCancellationVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (confirmCancellationVisibility) ConfirmCancellationBottomSheet(
                sheetState = confirmCancellationState,
                onDismissRequest = { showConfirmCancellation(false) },
                onConfirm = {
                    uiState.selectedOrder?.let { viewModel.cancelRide(it) }
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
    }

    fun showTariff(
        show: Boolean
    ) {
        tariffVisibility = show
        scope.launch { if (show) tariffState.show() else tariffState.hide() }
    }

    fun showSearchLocation(
        show: Boolean,
        forDest: Boolean = false
    ) {
        if (show) {
            if (forDest) {
                searchLocationVisibility = SearchLocationVisibility.END
                scope.launch { searchLocationState.show() }
            } else {
                searchLocationVisibility = SearchLocationVisibility.START
                scope.launch { searchLocationState.show() }
            }
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

    fun showOptions(
        show: Boolean
    ) {
        optionsVisibility = show
        scope.launch { if (show) optionsState.show() else optionsState.hide() }
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
}