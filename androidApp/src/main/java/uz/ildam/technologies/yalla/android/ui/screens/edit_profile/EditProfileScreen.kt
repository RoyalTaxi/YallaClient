package uz.ildam.technologies.yalla.android.ui.screens.edit_profile

import DatePickerBottomSheet
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.GenderButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.text_field.YallaTextField
import uz.ildam.technologies.yalla.android.utils.formatWithDotsDMY
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    scaffoldState: BottomSheetScaffoldState,
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        containerColor = YallaTheme.color.white,
        sheetContainerColor = YallaTheme.color.gray2,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            if (uiState.isDatePickerVisible) {
                DatePickerBottomSheet(
                    startDate = uiState.birthday ?: LocalDate.now(),
                    onSelectDate = { onIntent(EditProfileIntent.OnChangeBirthday(it)) },
                    onDismissRequest = { onIntent(EditProfileIntent.CloseDateBottomSheet) }
                )
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            ProfileContent(
                uiState = uiState,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            )

            // Dim overlay when the bottom sheet is visible
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
fun ProfileContent(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(onNavigateBack = { onIntent(EditProfileIntent.OnNavigateBack) })
        Spacer(modifier = Modifier.height(40.dp))
        ProfileImage(uiState = uiState, onIntent = onIntent)
        Spacer(modifier = Modifier.height(20.dp))
        ProfileForm(uiState = uiState, onIntent = onIntent)
        Spacer(modifier = Modifier.weight(1f))
        SaveButton(onClick = { onIntent(EditProfileIntent.OnSave) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
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
}

@Composable
fun ProfileImage(
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
            model = uiState.imageUrl,
            contentDescription = null,
            error = painterResource(R.drawable.img_default_pfp),
            placeholder = painterResource(R.drawable.img_default_pfp),
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    }
}

@Composable
fun ProfileForm(
    uiState: EditProfileUIState,
    onIntent: (EditProfileIntent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        YallaTextField(
            text = uiState.name,
            onChangeText = { onIntent(EditProfileIntent.OnChangeName(it)) },
            placeHolderText = stringResource(id = R.string.name),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        YallaTextField(
            text = uiState.surname,
            onChangeText = { onIntent(EditProfileIntent.OnChangeSurname(it)) },
            placeHolderText = stringResource(id = R.string.surname),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        YallaTextField(
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
fun GenderSelection(
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
fun SaveButton(onClick: () -> Unit) {
    YallaButton(
        text = stringResource(R.string.save),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = onClick
    )
}