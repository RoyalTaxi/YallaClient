package uz.ildam.technologies.yalla.android.ui.screens.address

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.ExperimentalMaterial3Api
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
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog
import uz.ildam.technologies.yalla.android.ui.sheets.ConfirmationBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.search_address.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheet
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressRoute(
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

                    AddressActionState.Error -> false
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
                AddressIntent.OpenSearchSheet -> searchLocationVisibility = true
            }
        }
    )

    AnimatedVisibility(
        visible = searchLocationVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (searchLocationVisibility) SearchByNameBottomSheet(
            initialAddress = uiState.selectedAddress?.name,
            sheetState = searchLocationState,
            isForDestination = false,
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
            onDismissRequest = { searchLocationVisibility = false }
        )
    }

    if (openMapVisibility) SelectFromMapBottomSheet(
        isForDestination = false,
        onSelectLocation = { name, location, _ ->
            viewModel.updateSelectedAddress(
                AddressUIState.Location(
                    name = name,
                    lat = location.latitude,
                    lng = location.longitude
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