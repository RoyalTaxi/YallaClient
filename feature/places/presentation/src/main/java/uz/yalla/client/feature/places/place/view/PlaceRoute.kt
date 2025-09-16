package uz.yalla.client.feature.places.place.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.core.common.sheet.AddDestinationBottomSheet
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.common.sheet.select_from_map.view.SelectFromMapScreen
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.places.place.intent.PlaceSideEffect
import uz.yalla.client.feature.places.place.model.*
import uz.yalla.client.feature.places.place.navigation.FromPlace
import uz.yalla.client.feature.places.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddressRoute(
    id: Int?,
    type: PlaceType,
    navigateTo: (FromPlace) -> Unit,
    viewModel: PlaceViewModel = koinViewModel(
        parameters = { parametersOf(id, type) }
    )
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            PlaceSideEffect.NavigateBack -> navigateTo(FromPlace.NavigateBack)
            PlaceSideEffect.ConfirmCancellation -> confirmCancellationState.show()
        }
    }

    AddressScreen(
        id = id,
        place = state.place,
        saveButtonState = state.isSaveButtonEnabled,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )


    if (state.isSearchVisible) {
        AddDestinationBottomSheet(
            sheetState = searchLocationState,
            onAddressSelected = { location ->
                viewModel.setSearchVisibility(false)
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
            onClickMap = { viewModel.setMapVisibility(true) },
            onDismissRequest = { viewModel.setSearchVisibility(false) },
        )
    }

    if (state.isMapVisible) {
        SelectFromMapScreen(
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
            onDismissRequest = { viewModel.setMapVisibility(false) }
        )
    }


    if (state.isConfirmationVisible) {
        ConfirmationBottomSheet(
            sheetState = confirmCancellationState,
            title = stringResource(R.string.delete_selected_address),
            actionText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onDismissRequest = {
                viewModel.setConfirmationVisibility(false)
                scope.launch { confirmCancellationState.hide() }
            },
            onConfirm = { state.deleteId?.let { viewModel.deleteOneAddress(it) } }
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