package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.CoroutineScope
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchForCarsBottomSheet

class MapSheetHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: MapViewModel,
    private val actionHandler: MapActionHandler,
    private val bottomSheetHandler: MapBottomSheetHandler
) {
    private var orderTaxiVisibility by mutableStateOf(true)
    private var searchCarsVisibility by mutableStateOf(false)

    @Composable
    fun Sheets(
        isLoading: Boolean,
        currentLatLng: MutableState<MapPoint>,
        uiState: MapUIState
    ) {
        AnimatedVisibility(
            visible = orderTaxiVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (orderTaxiVisibility) OrderTaxiBottomSheet(
                isLoading = isLoading,
                uiState = uiState,
                onSelectTariff = { tariff, wasSelected ->
                    if (wasSelected) bottomSheetHandler.showTariff(
                        show = true
                    )
                    else viewModel.updateSelectedTariff(
                        tariff = tariff
                    )
                },
                onCurrentLocationClick = {
                    bottomSheetHandler.showSearchLocation(
                        show = true,
                        forDest = false
                    )
                },
                onDestinationClick = {
                    if (uiState.destinations.isEmpty()) bottomSheetHandler.showSearchLocation(
                        show = true,
                        forDest = true
                    )
                    else bottomSheetHandler.showDestinations(
                        show = true
                    )
                },
                onSetOptionsClick = {
                    bottomSheetHandler.showOptions(
                        show = true
                    )
                },
                onCreateOrder = {
                    viewModel.orderTaxi()
                }
            )
        }

        AnimatedVisibility(
            visible = searchCarsVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (searchCarsVisibility) SearchForCarsBottomSheet(
                currentLocation = currentLatLng.value,
                uiState = uiState,
                viewModel = viewModel,
                onClickCancel = {
                    uiState.selectedOrder?.let { viewModel.cancelRide(it) }
                },
                onClickDetails = {

                }
            )
        }
    }

    fun showOrderTaxi() {
        hideAllSheets()
        orderTaxiVisibility = true
    }

    fun showSearchCars() {
        hideAllSheets()
        searchCarsVisibility = true
    }

    private fun hideAllSheets() {
        orderTaxiVisibility = false
        searchCarsVisibility = false
    }
}