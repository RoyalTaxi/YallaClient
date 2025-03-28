package uz.yalla.client.feature.places.place.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapView
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.feature.order.domain.model.type.PlaceType
import uz.yalla.client.feature.places.place.model.PlaceActionState
import uz.yalla.client.feature.places.place.model.PlaceUIState
import uz.yalla.client.feature.places.place.model.PlaceViewModel
import uz.yalla.client.feature.places.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddressRoute(
    id: Int?,
    type: PlaceType,
    onNavigateBack: () -> Unit,
    viewModel: PlaceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var deleteId by remember { mutableIntStateOf(-1) }
    var searchLocationVisibility by remember { mutableStateOf(false) }
    var openMapVisibility by remember { mutableStateOf(false) }
    var confirmCancellationVisibility by remember { mutableStateOf(false) }
    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            if (id != null) viewModel.findOneAddress(id)
            viewModel.updateType(type)
        }

        launch(Dispatchers.Main) {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    PlaceActionState.DeleteSuccess -> {
                        onNavigateBack()
                        false
                    }

                    is PlaceActionState.Error -> {
                        launch {
                            snackbarHostState.showSnackbar(
                                message = action.errorMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                        false
                    }

                    PlaceActionState.GetSuccess -> false
                    PlaceActionState.Loading -> true
                    PlaceActionState.PutSuccess -> {
                        onNavigateBack()
                        false
                    }
                }
            }
        }
    }

    AddressScreen(
        id = id,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = { intent ->
            when (intent) {
                is PlaceIntent.OnNavigateBack -> onNavigateBack()
                is PlaceIntent.OnSave -> {
                    if (id == null) viewModel.createOneAddress()
                    else viewModel.updateOneAddress(id)
                }

                is PlaceIntent.OnChangeName -> viewModel.updateName(intent.value)
                is PlaceIntent.OnDelete -> {
                    confirmCancellationVisibility = true
                    scope.launch { confirmCancellationState.show() }
                    deleteId = intent.id
                }

                is PlaceIntent.OnChangeApartment -> viewModel.updateApartment(intent.value)
                is PlaceIntent.OnChangeComment -> viewModel.updateComment(intent.value)
                is PlaceIntent.OnChangeEntrance -> viewModel.updateEnter(intent.value)
                is PlaceIntent.OnChangeFloor -> viewModel.updateFloor(intent.value)
                is PlaceIntent.OpenSearchSheet -> searchLocationVisibility = true
            }
        }
    )


        if (searchLocationVisibility) AddDestinationBottomSheet(
            sheetState = searchLocationState,
            onAddressSelected = { location ->
                searchLocationVisibility = false
                location.name?.let { name ->
                    location.point?.let { point ->
                        viewModel.updateSelectedAddress(
                            PlaceUIState.Location(
                                name = name,
                                lat = point.lat.or0(),
                                lng = point.lng.or0()
                            )
                        )
                    }
                }
            },
            onClickMap = { openMapVisibility = true },
            onDismissRequest = { searchLocationVisibility = false },
        )

    if (openMapVisibility) SelectFromMapView(
        startingPoint = null,
        viewValue = SelectFromMapViewValue.FOR_START,
        onSelectLocation = { location ->
            location.name?.let { name ->
                location.point?.let { point ->
                    viewModel.updateSelectedAddress(
                        PlaceUIState.Location(
                            name = name,
                            lat = point.lat.or0(),
                            lng = point.lng.or0()
                        )
                    )
                }
            }
        },
        onDismissRequest = { openMapVisibility = false }
    )

    AnimatedVisibility(
        visible = confirmCancellationVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (confirmCancellationVisibility) ConfirmationBottomSheet(
            sheetState = confirmCancellationState,
            title = stringResource(R.string.delete_selected_address),
            actionText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onDismissRequest = {
                confirmCancellationVisibility = false
                scope.launch { confirmCancellationState.hide() }
            },
            onConfirm = { viewModel.deleteOneAddress(deleteId) }
        )
    }

    if (loading) LoadingDialog()
}