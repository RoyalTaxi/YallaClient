package uz.yalla.client.feature.android.places.address.view

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.yalla.client.feature.android.places.R
import uz.yalla.client.feature.android.places.address.model.AddressActionState
import uz.yalla.client.feature.android.places.address.model.AddressUIState
import uz.yalla.client.feature.android.places.address.model.AddressViewModel
import uz.yalla.client.feature.core.dialogs.LoadingDialog
import uz.yalla.client.feature.core.sheets.AddDestinationBottomSheet
import uz.yalla.client.feature.core.sheets.ConfirmationBottomSheet
import uz.yalla.client.feature.core.sheets.search_address.SearchByNameBottomSheet
import uz.yalla.client.feature.core.sheets.select_from_map.SelectFromMapBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddressRoute(
    id: Int?,
    type: AddressType,
    onNavigateBack: () -> Unit,
    viewModel: AddressViewModel = koinViewModel()
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
        launch {
            if (id != null) viewModel.findOneAddress(id)
            viewModel.updateType(type)
        }

        launch {
            viewModel.actionState.collectLatest { action ->
                loading = when (action) {
                    AddressActionState.DeleteSuccess -> {
                        onNavigateBack()
                        false
                    }

                    is AddressActionState.Error -> {
                        launch {
                            snackbarHostState.showSnackbar(
                                message = action.errorMessage,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                        false
                    }

                    AddressActionState.GetSuccess -> false
                    AddressActionState.Loading -> true
                    AddressActionState.PutSuccess -> {
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
                is AddressIntent.OnNavigateBack -> onNavigateBack()
                is AddressIntent.OnSave -> {
                    if (id == null) viewModel.createOneAddress()
                    else viewModel.updateOneAddress(id)
                }

                is AddressIntent.OnChangeName -> viewModel.updateName(intent.value)
                is AddressIntent.OnDelete -> {
                    confirmCancellationVisibility = true
                    scope.launch { confirmCancellationState.show() }
                    deleteId = intent.id
                }

                is AddressIntent.OnChangeApartment -> viewModel.updateApartment(intent.value)
                is AddressIntent.OnChangeComment -> viewModel.updateComment(intent.value)
                is AddressIntent.OnChangeEntrance -> viewModel.updateEnter(intent.value)
                is AddressIntent.OnChangeFloor -> viewModel.updateFloor(intent.value)
                is AddressIntent.OpenSearchSheet -> searchLocationVisibility = true
            }
        }
    )

    AnimatedVisibility(
        visible = searchLocationVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (searchLocationVisibility) AddDestinationBottomSheet(
            sheetState = searchLocationState,
            onAddressSelected = { name, lat, lng, _ ->
                searchLocationVisibility = false
                viewModel.updateSelectedAddress(
                    AddressUIState.Location(
                        name = name,
                        lat = lat,
                        lng = lng
                    )
                )
            },
            onClickMap = { openMapVisibility = true },
            onDismissRequest = { searchLocationVisibility = false },
        )
    }

    if (openMapVisibility) SelectFromMapBottomSheet(
        isForDestination = false,
        isForNewDestination = false,
        onSelectLocation = { name, lat, lng, _ ->
            viewModel.updateSelectedAddress(
                AddressUIState.Location(
                    name = name,
                    lat = lat,
                    lng = lng
                )
            )
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