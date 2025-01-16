package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import uz.ildam.technologies.yalla.android.ui.sheets.ClientWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.DriverWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.FeedbackBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OnTheRideBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchForCarsBottomSheet
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import java.util.Locale

class MapSheetHandler(
    private val viewModel: MapViewModel,
    private val bottomSheetHandler: MapBottomSheetHandler
) {
    private var orderTaxiVisibility by mutableStateOf(true)
    private var searchCarsVisibility by mutableStateOf(false)
    private var clientWaitingVisibility by mutableStateOf(false)
    private var driverWaitingVisibility by mutableStateOf(false)
    private var onTheRideVisibility by mutableStateOf(false)
    private var feedBackVisibility by mutableStateOf(false)

    @Composable
    fun Sheets(
        isLoading: Boolean,
        currentLatLng: MutableState<MapPoint>,
        uiState: MapUIState,
        onCreateOrder: () -> Unit,
    ) {
        val context = LocalContext.current
        var timer by remember { mutableStateOf("") }
        var rating by remember { mutableIntStateOf(0) }
        val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

        LaunchedEffect(driverWaitingVisibility) {
            viewModel
                .infiniteTimer(driverWaitingVisibility)
                .collectLatest { seconds ->
                    val minutes = seconds / 60
                    val sec = seconds % 60

                    timer = String.format(Locale.US, "%02d:%02d", minutes, sec)
                }
        }

        AnimatedVisibility(
            visible = orderTaxiVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            if (orderTaxiVisibility) OrderTaxiBottomSheet(
                isLoading = isLoading,
                uiState = uiState,
                listState = listState,
                onSelectTariff = { tariff, wasSelected ->
                    if (wasSelected) bottomSheetHandler.showTariff(
                        show = true
                    )
                    else viewModel.setSelectedTariff(
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
                onAddNewDestinationClick = {
                    bottomSheetHandler.showSearchLocation(
                        show = true,
                        forDest = true
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
                    onCreateOrder()
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
            uiState.selectedDriver?.executor?.let {
                if (clientWaitingVisibility && uiState.selectedDriver.status == OrderStatus.Appointed) ClientWaitingBottomSheet(
                    car = it,
                    onClickCall = { number ->
                        val intent = Intent(ACTION_DIAL).apply {
                            data = Uri.parse("tel:$number")
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = driverWaitingVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedDriver?.executor?.let {
                if (driverWaitingVisibility && uiState.selectedDriver.status == OrderStatus.AtAddress) {
                    DriverWaitingBottomSheet(
                        car = it,
                        timer = timer,
                        onCancel = { bottomSheetHandler.showConfirmCancellation(true) },
                        onClickCall = { number ->
                            val intent = Intent(ACTION_DIAL).apply {
                                data = Uri.parse("tel:$number")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                        },
                        onOptionsClick = {}
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = onTheRideVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedDriver?.executor?.let {
                if (onTheRideVisibility && uiState.selectedDriver.status == OrderStatus.InFetters)
                    OnTheRideBottomSheet(car = it)
            }
        }

        AnimatedVisibility(
            visible = feedBackVisibility,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
        ) {
            uiState.selectedDriver?.executor?.let {
                if (feedBackVisibility && uiState.selectedDriver.status == OrderStatus.Completed)
                    FeedbackBottomSheet(
                        userRating = rating,
                        orderModel = uiState.selectedDriver,
                        onRatingChange = { rating = it },
                        onRate = {
                            viewModel.completeOrder()
                            viewModel.rateTheRide(rating)
                            rating = 0
                        },
                        onDismissRequest = {
                            viewModel.completeOrder()
                            rating = 0
                            showOrderTaxi()
                        }
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

    fun showOnTheRide() {
        hideAllSheets()
        onTheRideVisibility = true
    }

    fun showFeedback() {
        hideAllSheets()
        feedBackVisibility = true
    }

    private fun hideAllSheets() {
        orderTaxiVisibility = false
        searchCarsVisibility = false
        clientWaitingVisibility = false
        driverWaitingVisibility = false
        onTheRideVisibility = false
        feedBackVisibility = false
    }
}