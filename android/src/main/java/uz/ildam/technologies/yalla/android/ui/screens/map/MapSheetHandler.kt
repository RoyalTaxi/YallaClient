package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Intent
import android.content.Intent.ACTION_DIAL
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.ui.sheets.ActiveOrderDetailsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.ClientWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.DriverWaitingBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.FeedbackBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.MainBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.NoServiceBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OnTheRideBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchForCarsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoAction
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoBottomSheet
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import uz.yalla.client.feature.core.sheets.SheetValue
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
class MapSheetHandler(
    private val viewModel: MapViewModel,
    private val bottomSheetHandler: MapBottomSheetHandler,
    private val scaffoldState: BottomSheetScaffoldState<SheetValue>
) {
    private var visibleSheet by mutableStateOf<SheetType?>(SheetType.OrderTaxi)

    @Composable
    fun Sheets(
        isLoading: Boolean,
        currentLatLng: MutableState<MapPoint>,
        uiState: MapUIState
    ) {
        val context = LocalContext.current
        var timer by remember { mutableStateOf("") }
        var rating by remember { mutableIntStateOf(0) }
        val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(visibleSheet) {
            if (visibleSheet == SheetType.DriverWaiting) {
                viewModel.infiniteTimer(true).collectLatest { seconds ->
                    val minutes = seconds / 60
                    val sec = seconds % 60
                    timer = String.format(Locale.US, "%02d:%02d", minutes, sec)
                }
            }
        }


        when (visibleSheet) {
            SheetType.OrderTaxi -> MainBottomSheet(
                scaffoldState = scaffoldState,
                isLoading = isLoading,
                hasSelectedTariff = uiState.selectedTariff != null,
                orderTaxi = {
                    OrderTaxiBottomSheet(
                        isLoading = isLoading,
                        uiState = uiState,
                        listState = listState,
                        onSelectTariff = { tariff, wasSelected ->
                            scope.launch {
                                if (wasSelected) scaffoldState.sheetState.animateTo(
                                    SheetValue.Expanded
                                )
                                else {
                                    viewModel.setSelectedTariff(tariff = tariff)
                                    viewModel.tariffContainsSelectedOptions()
                                }
                            }
                        },
                        onCurrentLocationClick = {
                            bottomSheetHandler.showSearchLocation(
                                show = true,
                                forDest = false
                            )
                        },
                        onDestinationClick = {
                            if (uiState.destinations.size < 2) bottomSheetHandler.showSearchLocation(
                                show = true,
                                forDest = true
                            )
                            else bottomSheetHandler.showDestinations(true)
                        },
                        onAddNewDestinationClick = { bottomSheetHandler.addDestination(true) },
                        onAppear = viewModel::setPrimarySheetHeight
                    )
                },
                tariffInfo = {
                    TariffInfoBottomSheet(
                        uiState = uiState,
                        clearOptions = viewModel::clearOptions,
                        onOptionsChange = viewModel::setSelectedOptions
                    ) {
                        if (it == TariffInfoAction.OnClickComment) bottomSheetHandler.showOrderComment(
                            true
                        )
                    }
                }
            )

            SheetType.SearchCars -> SearchForCarsBottomSheet(
                currentLocation = currentLatLng.value,
                uiState = uiState,
                viewModel = viewModel,
                onClickCancel = { bottomSheetHandler.showConfirmCancellation(true) },
                onClickDetails = { showActiveOrderDetails() },
                onAppear = viewModel::setPrimarySheetHeight
            )

            SheetType.ClientWaiting -> uiState.selectedDriver?.executor?.takeIf {
                uiState.selectedDriver.status == OrderStatus.Appointed
            }?.let {
                ClientWaitingBottomSheet(
                    car = it,
                    onClickCall = createDialIntent(context),
                    onAppear = viewModel::setPrimarySheetHeight
                )
            }

            SheetType.DriverWaiting -> uiState.selectedDriver?.executor?.takeIf {
                uiState.selectedDriver.status == OrderStatus.AtAddress
            }?.let {
                DriverWaitingBottomSheet(
                    car = it,
                    timer = timer,
                    onCancel = { bottomSheetHandler.showConfirmCancellation(true) },
                    onClickCall = createDialIntent(context),
                    onClickDetails = { showActiveOrderDetails() },
                    onAppear = viewModel::setPrimarySheetHeight
                )
            }

            SheetType.OnTheRide -> uiState.selectedDriver?.executor?.takeIf {
                uiState.selectedDriver.status == OrderStatus.InFetters
            }?.let {
                OnTheRideBottomSheet(
                    car = it,
                    onAppear = viewModel::setPrimarySheetHeight
                )
            }

            SheetType.Feedback -> uiState.selectedDriver?.executor?.takeIf {
                uiState.selectedDriver.status == OrderStatus.Completed
            }?.let {
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
                    },
                    onAppear = viewModel::setPrimarySheetHeight
                )
            }

            SheetType.ActiveOrderDetails -> ActiveOrderDetailsBottomSheet(
                uiState = uiState,
                onClose = { showSearchCars() }
            )

            SheetType.NoService -> {
                NoServiceBottomSheet(
                    setCurrentLocation = {
                        bottomSheetHandler.showSearchLocation(
                            show = true,
                            forDest = false
                        )
                    }
                )
            }

            else -> {

            }
        }
    }

    fun showOrderTaxi() {
        visibleSheet = SheetType.OrderTaxi
    }

    fun showSearchCars() {
        visibleSheet = SheetType.SearchCars
    }

    fun showClientWaiting() {
        visibleSheet = SheetType.ClientWaiting
    }

    fun showDriverWaiting() {
        visibleSheet = SheetType.DriverWaiting
    }

    fun showOnTheRide() {
        visibleSheet = SheetType.OnTheRide
    }

    fun showFeedback() {
        visibleSheet = SheetType.Feedback
    }

    fun showActiveOrderDetails() {
        visibleSheet = SheetType.ActiveOrderDetails
    }

    fun showNoService() {
        visibleSheet = SheetType.NoService
    }

    private fun createDialIntent(context: android.content.Context): (String) -> Unit = { number ->
        val intent = Intent(ACTION_DIAL).apply { data = "tel:$number".toUri() }
        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
    }
}

enum class SheetType {
    OrderTaxi, SearchCars, ClientWaiting, DriverWaiting, OnTheRide, Feedback, NoService, ActiveOrderDetails
}
