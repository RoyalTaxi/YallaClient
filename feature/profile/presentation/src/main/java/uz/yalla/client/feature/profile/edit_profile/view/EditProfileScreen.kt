package uz.yalla.client.feature.profile.edit_profile.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.button.GenderButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PrimaryTextField
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.sheet.DatePickerModalBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.profile.presentation.R
import uz.yalla.client.feature.profile.edit_profile.components.Gender
import uz.yalla.client.feature.profile.edit_profile.model.EditProfileUIState
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileScreen(
    uiState: EditProfileUIState,
    snackbarHostState: SnackbarHostState,
    onIntent: (EditProfileIntent) -> Unit
) {
    val scrollState = rememberScrollState()
    val datePickerSheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = YallaTheme.color.background,
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            EditProfileTopBar(
                onNavigateBack = { onIntent(EditProfileIntent.NavigateBack) },
                onDelete = { onIntent(EditProfileIntent.DeleteProfile) }
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
                        contentColor = YallaTheme.color.background
                    )
                }
            )
        }
    ) { paddingValues ->
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

        if (uiState.isDatePickerVisible) {
            DatePickerModalBottomSheet(
                sheetState = datePickerSheetState,
                startDate = uiState.birthday ?: LocalDate.now(),
                onSelectDate = { onIntent(EditProfileIntent.ChangeBirthday(it)) },
                onDismissRequest = { onIntent(EditProfileIntent.CloseDateBottomSheet) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTopBar(
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_sing_out),
                    contentDescription = null,
                    tint = YallaTheme.color.red
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.edit_profile),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.labelLarge
            )
        }
    )
}

@Composable
private fun ProfileContent(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(
            imageUrl = uiState.imageUrl,
            newImage = uiState.newImage,
            onUpdateProfile = { onIntent(EditProfileIntent.UpdateImage) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        ProfileHeaderInfo(
            fullName = "${uiState.name} ${uiState.surname}",
            phone = uiState.phone
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileForm(uiState = uiState, onIntent = onIntent)

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))

        SaveButton(
            onClick = {
                if (uiState.hasChanges) {
                    onIntent(EditProfileIntent.SaveProfile)
                } else {
                    onIntent(EditProfileIntent.NavigateBack)
                }
            },
            hasChanges = uiState.hasChanges
        )
    }
}

@SuppressLint("RememberReturnType")
@Composable
private fun ProfileImage(
    imageUrl: String,
    newImage: ByteArray? = null,
    onUpdateProfile: () -> Unit
) {
    val bitmap = remember(newImage) {
        newImage?.let {
            BitmapFactory.decodeStream(ByteArrayInputStream(it))
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        val imageModifier = Modifier
            .clip(CircleShape)
            .size(100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Image,
                onClick = onUpdateProfile
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
                    .data(imageUrl)
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

        Box(
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    spotColor = YallaTheme.color.black.copy(alpha = 0.5f)
                )
                .background(color = YallaTheme.color.background, shape = CircleShape)
                .border(1.dp, YallaTheme.color.background, CircleShape)
                .clickable(onClick = onUpdateProfile),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.padding(9.dp),
                tint = YallaTheme.color.onBackground
            )
        }
    }
}

@Composable
private fun ProfileHeaderInfo(
    phone: String,
    fullName: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (fullName.isNotBlank()) {
            Text(
                text = fullName,
                style = YallaTheme.font.title,
                color = YallaTheme.color.onBackground,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = phone,
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileForm(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PrimaryTextField(
            text = uiState.name,
            onChangeText = { onIntent(EditProfileIntent.ChangeName(it)) },
            placeHolderText = stringResource(id = R.string.name),
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryTextField(
            text = uiState.surname,
            onChangeText = { onIntent(EditProfileIntent.ChangeSurname(it)) },
            placeHolderText = stringResource(id = R.string.surname),
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryTextField(
            text = uiState.birthday?.formatWithDotsDMY() ?: "",
            placeHolderText = stringResource(id = R.string.date_of_birth),
            onChangeText = {},
            trailingIcon = painterResource(id = R.drawable.ic_calendar),
            onClick = { onIntent(EditProfileIntent.OpenDateBottomSheet) },
            modifier = Modifier.fillMaxWidth()
        )

        GenderSelection(
            gender = uiState.gender,
            onIntent = onIntent
        )
    }
}

@Composable
private fun GenderSelection(
    gender: Gender = Gender.NotSelected,
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
            isSelected = gender == Gender.Male,
            onSelect = { onIntent(EditProfileIntent.ChangeGender(Gender.Male)) }
        )
        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_f),
            isSelected = gender == Gender.Female,
            onSelect = { onIntent(EditProfileIntent.ChangeGender(Gender.Female)) }
        )
    }
}

@Composable
private fun SaveButton(
    hasChanges: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = stringResource(if (hasChanges) R.string.save else R.string.close),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = onClick,
        containerColor = if (hasChanges) YallaTheme.color.black else YallaTheme.color.surface,
        contentColor = if (hasChanges) YallaTheme.color.onBlack else YallaTheme.color.onSurface,
    )
}
