package uz.ildam.technologies.yalla.android.ui.screens.map

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.ui.sheets.ArrangeDestinationsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SetOrderOptionsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheet
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.core.data.mapper.or0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheetHandler(
    uiState: MapUIState,
    currentLatLng: MutableState<MapPoint>,

    searchForLocationSheetVisibility: MutableState<SearchForLocationBottomSheetVisibility>,
    selectFromMapSheetVisibility: MutableState<SelectFromMapSheetVisibility>,
    arrangeDestinationsSheetVisibility: MutableState<Boolean>,
    tariffBottomSheetVisibility: MutableState<Boolean>,
    setOrderOptionsBottomSheetVisibility: MutableState<Boolean>,

    searchForLocationSheetState: SheetState,
    arrangeDestinationsSheetState: SheetState,
    tariffBottomSheetState: SheetState,
    setOrderOptionsBottomSheetState: SheetState,
    mapActionHandler: MapActionHandler,
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = searchForLocationSheetVisibility.value != SearchForLocationBottomSheetVisibility.INVISIBLE,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (searchForLocationSheetVisibility.value != SearchForLocationBottomSheetVisibility.INVISIBLE) SearchByNameBottomSheet(
            sheetState = searchForLocationSheetState,
            foundAddresses = uiState.foundAddresses,
            isForDestination = searchForLocationSheetVisibility.value == SearchForLocationBottomSheetVisibility.END,
            onAddressSelected = { dest ->
                if (searchForLocationSheetVisibility.value == SearchForLocationBottomSheetVisibility.START) {
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                        if (dest.addressId != 0) viewModel.getAddressDetails(
                            MapPoint(dest.lat, dest.lng)
                        )
                        else {
                            val result =
                                viewModel.isPointInsidePolygon(MapPoint(dest.lat, dest.lng))
                            if (result.first) {
                                viewModel.getAddressDetails(MapPoint(dest.lat, dest.lng))
                                viewModel.updateSelectedLocation(addressId = result.second)
                            } else Toast.makeText(context, "Out of service", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        currentLatLng.value = MapPoint(dest.lat, dest.lng)
                        mapActionHandler.moveCamera(
                            MapPoint(
                                lat = dest.lat,
                                lng = dest.lng
                            ),
                            animate = true
                        )
                    }
                } else if (searchForLocationSheetVisibility.value == SearchForLocationBottomSheetVisibility.END) {
                    val destinations = uiState.destinations.toMutableList()
                    destinations.add(
                        MapUIState.Destination(
                            dest.name,
                            MapPoint(dest.lat, dest.lng)
                        )
                    )
                    viewModel.updateDestinations(destinations)
                }
            },
            onSearchForAddress = {
                viewModel.searchForAddress(
                    query = it,
                    point = currentLatLng.value
                )
            },
            onClickMap = {
                selectFromMapSheetVisibility.value =
                    if (searchForLocationSheetVisibility.value == SearchForLocationBottomSheetVisibility.START) {
                        SelectFromMapSheetVisibility.START
                    } else {
                        SelectFromMapSheetVisibility.END
                    }
            },
            onDismissRequest = {
                scope.launch {
                    searchForLocationSheetVisibility.value =
                        SearchForLocationBottomSheetVisibility.INVISIBLE
                    searchForLocationSheetState.hide()
                    viewModel.updateUIState(foundAddresses = emptyList())
                }
            }
        )
    }


    if (selectFromMapSheetVisibility.value != SelectFromMapSheetVisibility.INVISIBLE) SelectFromMapBottomSheet(
        isForDestination = selectFromMapSheetVisibility.value == SelectFromMapSheetVisibility.END,
        onSelectLocation = { name, location, isForDestination ->
            if (isForDestination) {
                val destinations = uiState.destinations.toMutableList()
                destinations.add(
                    MapUIState.Destination(
                        name,
                        MapPoint(location.latitude, location.longitude)
                    )
                )
                viewModel.updateDestinations(destinations)
            } else {
                if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                    viewModel.getAddressDetails(MapPoint(location.latitude, location.longitude))
                } else {
                    mapActionHandler.moveCamera(
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
        onDismissRequest = {
            selectFromMapSheetVisibility.value = SelectFromMapSheetVisibility.INVISIBLE
        }
    )

    AnimatedVisibility(
        visible = arrangeDestinationsSheetVisibility.value,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (arrangeDestinationsSheetVisibility.value) ArrangeDestinationsBottomSheet(
            destinations = uiState.destinations,
            sheetState = arrangeDestinationsSheetState,
            onAddNewDestinationClick = {
                scope.launch {
                    searchForLocationSheetVisibility.value =
                        SearchForLocationBottomSheetVisibility.END
                    searchForLocationSheetState.show()
                }
            },
            onDismissRequest = { orderedDestinations ->
                scope.launch {
                    arrangeDestinationsSheetVisibility.value = false
                    arrangeDestinationsSheetState.hide()
                    viewModel.updateDestinations(orderedDestinations)

                    if (orderedDestinations.isEmpty()) getCurrentLocation(context) { location ->
                        currentLatLng.value = MapPoint(location.latitude, location.longitude)
                        mapActionHandler.moveCamera(
                            mapPoint = MapPoint(location.latitude, location.longitude),
                            animate = true
                        )
                    }
                }
            }
        )
    }

    AnimatedVisibility(
        visible = tariffBottomSheetVisibility.value,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        uiState.selectedTariff?.let { selectedTariff ->
            if (tariffBottomSheetVisibility.value) TariffInfoBottomSheet(
                sheetState = tariffBottomSheetState,
                tariff = selectedTariff,
                arrivingTime = uiState.timeout.or0(),
                onDismissRequest = {
                    scope.launch {
                        tariffBottomSheetVisibility.value = false
                        tariffBottomSheetState.hide()
                    }
                }
            )
        }
    }

    AnimatedVisibility(
        visible = setOrderOptionsBottomSheetVisibility.value,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        uiState.selectedTariff?.let { selectedTariff ->
            if (setOrderOptionsBottomSheetVisibility.value) SetOrderOptionsBottomSheet(
                sheetState = setOrderOptionsBottomSheetState,
                selectedTariff = selectedTariff,
                options = uiState.options,
                selectedOptions = uiState.selectedOptions,
                onSave = { options -> viewModel.updateSelectedOptions(options) },
                onDismissRequest = {
                    scope.launch {
                        setOrderOptionsBottomSheetVisibility.value = false
                        setOrderOptionsBottomSheetState.hide()
                    }
                }
            )
        }
    }
}