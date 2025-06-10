package uz.yalla.client.feature.places.place.view

import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapView
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.type.PlaceType
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
    val place by viewModel.place.collectAsState()
    val saveButtonState by viewModel.saveButtonState.collectAsState()
    val scope = rememberCoroutineScope()
    val loading by viewModel.loading.collectAsState()
    var deleteId by remember { mutableIntStateOf(-1) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isMapVisible by remember { mutableStateOf(false) }
    var isConfirmationVisible by remember { mutableStateOf(false) }
    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    val showErrorDialog by viewModel.showErrorDialog.collectAsState()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsState()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            if (id != null) viewModel.findOneAddress(id)
            viewModel.updateType(type)
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            viewModel.navigationChannel.collect {
                onNavigateBack()
            }
        }
    }

    AddressScreen(
        id = id,
        place = place,
        saveButtonState = saveButtonState,
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
                    isConfirmationVisible = true
                    scope.launch { confirmCancellationState.show() }
                    deleteId = intent.id
                }

                is PlaceIntent.OnChangeApartment -> viewModel.updateApartment(intent.value)
                is PlaceIntent.OnChangeComment -> viewModel.updateComment(intent.value)
                is PlaceIntent.OnChangeEntrance -> viewModel.updateEnter(intent.value)
                is PlaceIntent.OnChangeFloor -> viewModel.updateFloor(intent.value)
                is PlaceIntent.OpenSearchSheet -> isSearchVisible = true
            }
        }
    )


    if (isSearchVisible) {
        AddDestinationBottomSheet(
            sheetState = searchLocationState,
            onAddressSelected = { location ->
                isSearchVisible = false
                location.name?.let { name ->
                    location.point?.let { point ->
                        viewModel.updateSelectedAddress(
                            address = name,
                            lat = point.lat.or0(),
                            lng = point.lng.or0()
                        )
                    }
                }
            },
            onClickMap = { isMapVisible = true },
            onDismissRequest = { isSearchVisible = false },
        )
    }

    if (isMapVisible) {
        SelectFromMapView(
            startingPoint = null,
            viewValue = SelectFromMapViewValue.FOR_START,
            onSelectLocation = { location ->
                location.name?.let { name ->
                    location.point?.let { point ->
                        viewModel.updateSelectedAddress(
                            address = name,
                            lat = point.lat.or0(),
                            lng = point.lng.or0()
                        )
                    }
                }
            },
            onDismissRequest = { isMapVisible = false }
        )
    }


    if (isConfirmationVisible) {
        ConfirmationBottomSheet(
            sheetState = confirmCancellationState,
            title = stringResource(R.string.delete_selected_address),
            actionText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onDismissRequest = {
                isConfirmationVisible = false
                scope.launch { confirmCancellationState.hide() }
            },
            onConfirm = { viewModel.deleteOneAddress(deleteId) }
        )
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

    if (loading) {
        LoadingDialog()
    }
}