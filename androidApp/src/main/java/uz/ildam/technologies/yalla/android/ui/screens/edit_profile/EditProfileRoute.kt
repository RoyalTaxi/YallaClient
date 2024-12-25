package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

@Composable
fun EditProfileRoute(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { if (it != null) viewModel.setNewImage(it, context) }
    )

    LaunchedEffect(Unit) {
        launch { viewModel.getMe() }

        launch {
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
    }

    EditProfileScreen(
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is EditProfileIntent.OnNavigateBack -> onNavigateBack()
                is EditProfileIntent.OnChangeGender -> TODO()
                is EditProfileIntent.OnChangeName -> TODO()
                is EditProfileIntent.OnChangeSurname -> TODO()
                is EditProfileIntent.OnUpdateImage -> imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )

                EditProfileIntent.OnSave -> {
                    if (uiState.newImage != null) viewModel.updateAvatar()
                }
            }
        }
    )

    if (loading) LoadingDialog()
}