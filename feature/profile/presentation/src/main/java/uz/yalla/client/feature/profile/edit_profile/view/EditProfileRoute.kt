package uz.yalla.client.feature.profile.edit_profile.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.sheet.ConfirmationBottomSheet
import uz.yalla.client.core.common.system.isFileSizeTooLarge
import uz.yalla.client.core.common.system.isImageDimensionTooLarge
import uz.yalla.client.feature.profile.R
import uz.yalla.client.feature.profile.edit_profile.model.EditProfileViewModel
import uz.yalla.client.feature.profile.edit_profile.model.NavigationEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileRoute(
    onNavigateToStart: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val loading by viewModel.loading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(true)
    var sheetVisibility by remember { mutableStateOf(false) }

    val showErrorDialog by viewModel.showErrorDialog.collectAsState()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsState()

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
                ) viewModel.setNewImage(uri, context)
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
            viewModel.getMe()
        }

        launch(Dispatchers.Main) {
            viewModel.navigationChannel.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateBack -> onNavigateBack()
                    is NavigationEvent.NavigateToStart -> onNavigateToStart()
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
        onIntent = { intent ->
            when (intent) {
                is EditProfileIntent.OnNavigateBack -> onNavigateBack()
                is EditProfileIntent.OnDelete -> {
                    sheetVisibility = true
                    scope.launch { sheetState.show() }
                }

                is EditProfileIntent.OnChangeGender -> viewModel.changeGender(intent.gender)
                is EditProfileIntent.OnChangeName -> viewModel.changeName(intent.name)
                is EditProfileIntent.OnChangeSurname -> viewModel.changeSurname(intent.surname)
                is EditProfileIntent.OnChangeBirthday -> viewModel.changeBirthday(intent.birthday)

                is EditProfileIntent.OnUpdateImage -> {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                is EditProfileIntent.OnSave -> {
                    focusManager.clearFocus(force = true)
                    if (uiState.newImage != null) viewModel.updateAvatar()
                    else viewModel.postMe()
                }

                is EditProfileIntent.OpenDateBottomSheet -> {
                    viewModel.setDatePickerVisible(true)
                }

                is EditProfileIntent.CloseDateBottomSheet -> {
                    viewModel.setDatePickerVisible(false)
                }
            }
        }
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
            viewModel.logout()
        }
    )

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