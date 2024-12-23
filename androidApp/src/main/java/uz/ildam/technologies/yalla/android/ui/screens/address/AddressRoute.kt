package uz.ildam.technologies.yalla.android.ui.screens.address

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType

@Composable
fun AddressRoute(
    id: Int?,
    type: AddressType,
    onNavigateBack: () -> Unit,
    viewModel: AddressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

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
                is AddressIntent.OnAddressSelected -> viewModel.updateSelectedAddress(intent.address)
                is AddressIntent.OnDelete -> viewModel.deleteOneAddress(intent.id)
                is AddressIntent.OnChangeApartment -> viewModel.updateApartment(intent.value)
                is AddressIntent.OnChangeComment -> viewModel.updateComment(intent.value)
                is AddressIntent.OnChangeEntrance -> viewModel.updateEnter(intent.value)
                is AddressIntent.OnChangeFloor -> viewModel.updateFloor(intent.value)
            }
        }
    )

    if (loading) LoadingDialog()
}