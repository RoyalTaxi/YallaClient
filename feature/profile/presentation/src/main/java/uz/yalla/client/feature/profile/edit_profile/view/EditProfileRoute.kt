package uz.yalla.client.feature.profile.edit_profile.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.system.isFileSizeTooLarge
import uz.yalla.client.core.common.system.isImageDimensionTooLarge
import uz.yalla.client.feature.profile.R
import uz.yalla.client.feature.profile.edit_profile.model.EditProfileSideEffect
import uz.yalla.client.feature.profile.edit_profile.model.EditProfileViewModel
import uz.yalla.client.feature.profile.edit_profile.model.onIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileRoute(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(true)
    var sheetVisibility by remember { mutableStateOf(false) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) scope.launch {
                if (
                    context.isFileSizeTooLarge(uri, 4194304).not() ||
                    context.isImageDimensionTooLarge(uri, 1080, 1080).not()
                ) viewModel.onIntent(EditProfileIntent.SetNewImage(uri, context))
                else snackbarHostState.showSnackbar(
                    message = context.getString(R.string.error_size),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            viewModel.onIntent(EditProfileIntent.LoadProfile)
        }

        launch(Dispatchers.Main) {
            viewModel.container.sideEffectFlow.collect { sideEffect ->
                when (sideEffect) {
                    is EditProfileSideEffect.NavigateBack -> onNavigateBack()
                    is EditProfileSideEffect.ShowImagePicker -> {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    is EditProfileSideEffect.ShowDeleteConfirmation -> {
                        sheetVisibility = true
                        scope.launch { sheetState.show() }
                    }

                    is EditProfileSideEffect.ClearFocus -> {
                        focusManager.clearFocus(force = true)
                    }

                    is EditProfileSideEffect.ShowErrorMessage -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = sideEffect.message,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.isDatePickerVisible) {
        launch(Dispatchers.Main) {
            if (uiState.isDatePickerVisible) {
                bottomSheetScaffoldState.bottomSheetState.expand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
        }
    }

    EditProfileScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )

    if (sheetVisibility) ConfirmationBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.delete_profile_title),
        description = stringResource(R.string.delete_profile_desc),
        actionText = stringResource(R.string.exit),
        dismissText = stringResource(R.string.stay),
        onDismissRequest = {
            sheetVisibility = false
            scope.launch { sheetState.hide() }
        },
        onConfirm = {
            viewModel.onIntent(EditProfileIntent.ConfirmDeleteProfile)
        }
    )
}