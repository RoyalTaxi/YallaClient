package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import androidx.activity.compose.BackHandler
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileRoute(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    // BottomSheetScaffold uses a scaffold state with a bottom sheet state.
    // We can use rememberStandardBottomSheetState to control collapse/expand
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            // or skipHalfExpanded if you prefer
        )
    )

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.setNewImage(uri, context)
        }
    )

    // Collect side effects (e.g. loading, success, errors)
    LaunchedEffect(Unit) {
        // Fetch user data
        viewModel.getMe()

        // Collect actions
        viewModel.actionState.collectLatest { action ->
            loading = when (action) {
                EditProfileActionState.Error -> false
                EditProfileActionState.GetSuccess -> false
                EditProfileActionState.Loading -> true
                EditProfileActionState.UpdateAvatarSuccess -> {
                    // After updating the avatar, post the changes
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

    // Listen for changes in isDatePickerVisible and expand/collapse the sheet accordingly
    LaunchedEffect(uiState.isDatePickerVisible) {
        if (uiState.isDatePickerVisible) {
            // Expand the bottom sheet
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            // Collapse the bottom sheet
            bottomSheetScaffoldState.bottomSheetState.hide()
        }
    }

    // Render the screen with BottomSheetScaffold
    EditProfileScreen(
        scaffoldState = bottomSheetScaffoldState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                // Navigate back
                is EditProfileIntent.OnNavigateBack -> onNavigateBack()

                // Update name, surname, gender, etc.
                is EditProfileIntent.OnChangeGender -> viewModel.changeGender(intent.gender)
                is EditProfileIntent.OnChangeName -> viewModel.changeName(intent.name)
                is EditProfileIntent.OnChangeSurname -> viewModel.changeSurname(intent.surname)
                is EditProfileIntent.OnChangeBirthday -> viewModel.changeBirthday(intent.birthday)

                // Open image picker
                is EditProfileIntent.OnUpdateImage -> {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                // Save changes
                EditProfileIntent.OnSave -> {
                    if (uiState.newImage != null) {
                        viewModel.updateAvatar()
                    } else {
                        // If no new avatar, just update other fields
                        viewModel.postMe()
                    }
                }

                // Show date bottom sheet
                EditProfileIntent.OpenDateBottomSheet -> {
                    viewModel.setDatePickerVisible(true)
                }

                // Hide date bottom sheet
                EditProfileIntent.CloseDateBottomSheet -> {
                    viewModel.setDatePickerVisible(false)
                }
            }
        }
    )

    // Show loading dialog if needed
    if (loading) LoadingDialog()
}