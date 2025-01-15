package uz.yalla.client.feature.android.profile.edit_profile.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.profile.edit_profile.model.EditProfileActionState
import uz.yalla.client.feature.android.profile.edit_profile.model.EditProfileViewModel
import uz.yalla.client.feature.core.dialogs.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileRoute(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.setNewImage(uri, context)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getMe()

        viewModel.actionState.collectLatest { action ->
            loading = when (action) {
                EditProfileActionState.Error -> false
                EditProfileActionState.GetSuccess -> false
                EditProfileActionState.Loading -> true
                EditProfileActionState.UpdateAvatarSuccess -> {
                    launch { viewModel.postMe() }
                    false
                }

                EditProfileActionState.UpdateSuccess -> {
                    onNavigateBack()
                    false
                }
            }
        }
    }

    LaunchedEffect(uiState.isDatePickerVisible) {
        if (uiState.isDatePickerVisible) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            bottomSheetScaffoldState.bottomSheetState.hide()
        }
    }

    EditProfileScreen(
        scaffoldState = bottomSheetScaffoldState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is EditProfileIntent.OnNavigateBack -> onNavigateBack()

                is EditProfileIntent.OnChangeGender -> viewModel.changeGender(intent.gender)
                is EditProfileIntent.OnChangeName -> viewModel.changeName(intent.name)
                is EditProfileIntent.OnChangeSurname -> viewModel.changeSurname(intent.surname)
                is EditProfileIntent.OnChangeBirthday -> viewModel.changeBirthday(intent.birthday)

                is EditProfileIntent.OnUpdateImage -> {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                EditProfileIntent.OnSave -> {
                    focusManager.clearFocus(force = true)
                    if (uiState.newImage != null) viewModel.updateAvatar()
                    else viewModel.postMe()
                }

                EditProfileIntent.OpenDateBottomSheet -> {
                    viewModel.setDatePickerVisible(true)
                }

                EditProfileIntent.CloseDateBottomSheet -> {
                    viewModel.setDatePickerVisible(false)
                }
            }
        }
    )

    if (loading) LoadingDialog()
}