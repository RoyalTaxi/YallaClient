package uz.yalla.client.feature.android.profile.edit_profile.view

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import org.threeten.bp.LocalDate
import uz.yalla.client.feature.android.cancel.R
import uz.yalla.client.feature.android.profile.edit_profile.components.Gender
import uz.yalla.client.feature.android.profile.edit_profile.model.EditProfileUIState
import uz.yalla.client.feature.core.components.buttons.GenderButton
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.components.text_field.YTextField
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.sheets.DatePickerBottomSheet
import uz.yalla.client.feature.core.utils.formatWithDotsDMY
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileScreen(
    scaffoldState: BottomSheetScaffoldState,
    uiState: EditProfileUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (EditProfileIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        containerColor = YallaTheme.color.white,
        sheetContainerColor = YallaTheme.color.gray2,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(onClick = { onIntent(EditProfileIntent.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.edit_profile),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                }
            )
        },
        sheetContent = {
            if (uiState.isDatePickerVisible) DatePickerBottomSheet(
                startDate = uiState.birthday ?: LocalDate.now(),
                onSelectDate = { onIntent(EditProfileIntent.OnChangeBirthday(it)) },
                onDismissRequest = { onIntent(EditProfileIntent.CloseDateBottomSheet) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .consumeWindowInsets(WindowInsets.navigationBars.asPaddingValues()),
                snackbar = { snackbarData: SnackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = YallaTheme.color.red,
                        contentColor = YallaTheme.color.white
                    )
                }
            )
        }
    ) { paddingValues ->
        Box {
            ProfileContent(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .imePadding()
                    .verticalScroll(scrollState)
            )

            AnimatedVisibility(
                visible = scaffoldState.bottomSheetState.isVisible,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(targetAlpha = 1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            onClick = { onIntent(EditProfileIntent.CloseDateBottomSheet) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    }
}

@Composable
internal fun ProfileContent(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        ProfileImage(uiState = uiState, onIntent = onIntent)
        Spacer(modifier = Modifier.height(20.dp))
        ProfileForm(uiState = uiState, onIntent = onIntent)
        Spacer(modifier = Modifier.weight(1f))
        SaveButton(onClick = { onIntent(EditProfileIntent.OnSave) })
    }
}

@Composable
internal fun ProfileImage(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    val bitmap = remember(uiState.newImage) {
        uiState.newImage?.let {
            BitmapFactory.decodeStream(ByteArrayInputStream(it))
        }
    }

    val imageModifier = Modifier
        .clip(CircleShape)
        .size(100.dp)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Image,
            onClick = { onIntent(EditProfileIntent.OnUpdateImage) }
        )

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uiState.imageUrl)
                .size(1080)
                .allowHardware(false)
                .crossfade(true)
                .build(),
            contentDescription = null,
            error = painterResource(R.drawable.img_default_pfp),
            placeholder = painterResource(R.drawable.img_default_pfp),
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    }
}

@Composable
internal fun ProfileForm(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        YTextField(
            text = uiState.name,
            onChangeText = { onIntent(EditProfileIntent.OnChangeName(it)) },
            placeHolderText = stringResource(id = R.string.name),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        YTextField(
            text = uiState.surname,
            onChangeText = { onIntent(EditProfileIntent.OnChangeSurname(it)) },
            placeHolderText = stringResource(id = R.string.surname),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        YTextField(
            text = uiState.birthday?.formatWithDotsDMY() ?: "",
            placeHolderText = stringResource(id = R.string.date_of_birth),
            onChangeText = {},
            trailingIcon = painterResource(id = R.drawable.ic_calendar),
            onClick = { onIntent(EditProfileIntent.OpenDateBottomSheet) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        GenderSelection(uiState = uiState, onIntent = onIntent)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
internal fun GenderSelection(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_m),
            isSelected = uiState.gender == Gender.Male,
            onSelect = { onIntent(EditProfileIntent.OnChangeGender(Gender.Male)) }
        )
        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_f),
            isSelected = uiState.gender == Gender.Female,
            onSelect = { onIntent(EditProfileIntent.OnChangeGender(Gender.Female)) }
        )
    }
}

@Composable
internal fun SaveButton(onClick: () -> Unit) {
    YButton(
        text = stringResource(R.string.save),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = onClick
    )
}