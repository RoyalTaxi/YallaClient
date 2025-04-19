package uz.yalla.client.feature.profile.edit_profile.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import uz.yalla.client.core.common.button.GenderButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PrimaryTextField
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.sheet.DatePickerModalBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.profile.R
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
        containerColor = YallaTheme.color.white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            EditProfileTopBar(
                onNavigateBack = { onIntent(EditProfileIntent.OnNavigateBack) },
                onDelete = { onIntent(EditProfileIntent.OnDelete) }
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
                onSelectDate = { onIntent(EditProfileIntent.OnChangeBirthday(it)) },
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.black
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
                color = YallaTheme.color.black,
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
            onUpdateProfile = { onIntent(EditProfileIntent.OnUpdateImage) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileForm(uiState = uiState, onIntent = onIntent)

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(onClick = { onIntent(EditProfileIntent.OnSave) })
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
}

@Composable
private fun ProfileForm(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        PrimaryTextField(
            text = uiState.name,
            onChangeText = { onIntent(EditProfileIntent.OnChangeName(it)) },
            placeHolderText = stringResource(id = R.string.name),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryTextField(
            text = uiState.surname,
            onChangeText = { onIntent(EditProfileIntent.OnChangeSurname(it)) },
            placeHolderText = stringResource(id = R.string.surname),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryTextField(
            text = uiState.birthday?.formatWithDotsDMY() ?: "",
            placeHolderText = stringResource(id = R.string.date_of_birth),
            onChangeText = {},
            trailingIcon = painterResource(id = R.drawable.ic_calendar),
            onClick = { onIntent(EditProfileIntent.OpenDateBottomSheet) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        GenderSelection(
            gender = uiState.gender,
            onIntent = onIntent)

        Spacer(modifier = Modifier.height(8.dp))
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
            onSelect = { onIntent(EditProfileIntent.OnChangeGender(Gender.Male)) }
        )
        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_f),
            isSelected = gender == Gender.Female,
            onSelect = { onIntent(EditProfileIntent.OnChangeGender(Gender.Female)) }
        )
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    PrimaryButton(
        text = stringResource(R.string.save),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = onClick
    )
}