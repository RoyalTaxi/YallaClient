package uz.ildam.technologies.yalla.android.ui.screens.map

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
import uz.ildam.technologies.yalla.android.ui.sheets.ClientWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.DriverWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchForCarsBottomSheet
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus

class MapSheetHandler(
    private val viewModel: MapViewModel,
    private val bottomSheetHandler: MapBottomSheetHandler
) {
    private var orderTaxiVisibility by mutableStateOf(true)
    private var searchCarsVisibility by mutableStateOf(false)
    private var clientWaitingVisibility by mutableStateOf(false)
    private var driverWaitingVisibility by mutableStateOf(false)

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
                onSelectPaymentMethodClick = {
                    bottomSheetHandler.showPaymentMethod(
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
                onClickCancel = { bottomSheetHandler.showConfirmCancellation(true) },
                onClickDetails = {

                }
            )
        }

        AnimatedVisibility(
            visible = clientWaitingVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedDriver?.executor?.driver?.let {
                if (clientWaitingVisibility && uiState.selectedDriver.status == OrderStatus.Appointed) ClientWaitingBottomSheet(
                    car = it,
                    onDismissRequest = {}
                )
            }
        }

        AnimatedVisibility(
            visible = driverWaitingVisibility
        ) {
            uiState.selectedDriver?.executor?.driver?.let {
                if (driverWaitingVisibility && uiState.selectedDriver.status == OrderStatus.AtAddress) DriverWaitingBottomSheet(
                    car = it,
                    timer = "",
                    onCancel = { bottomSheetHandler.showConfirmCancellation(true) },
                    onOptionsClick = {}
                )
            }
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

    fun showClientWaiting() {
        hideAllSheets()
        clientWaitingVisibility = true
    }

    fun showDriverWaiting() {
        hideAllSheets()
        driverWaitingVisibility = true
    }

    private fun hideAllSheets() {
        orderTaxiVisibility = false
        searchCarsVisibility = false
    }
}