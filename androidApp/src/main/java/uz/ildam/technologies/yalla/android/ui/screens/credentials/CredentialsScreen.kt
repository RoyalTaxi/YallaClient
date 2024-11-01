package uz.ildam.technologies.yalla.android.ui.screens.credentials

import DatePickerBottomSheet
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.GenderButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.textfield.YallaTextField
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDotsDMY


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CredentialsScreen(
    sheetState: BottomSheetScaffoldState,
    uiState: CredentialsUIState,
    onIntent: (CredentialsIntent) -> Unit
) {

    BackHandler(onBack = { onIntent(CredentialsIntent.NavigateBack) })

    BottomSheetScaffold(
        sheetDragHandle = null,
        sheetSwipeEnabled = false,
        scaffoldState = sheetState,
        containerColor = YallaTheme.color.white,
        sheetContainerColor = YallaTheme.color.gray2,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            DatePickerBottomSheet(
                startDate = uiState.dateOfBirth ?: LocalDate.now(),
                onSelectDate = { onIntent(CredentialsIntent.SetDateOfBirth(it)) },
                onDismissRequest = { onIntent(CredentialsIntent.CloseDateBottomSheet) }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YallaTheme.color.white)
                    .padding(20.dp)
                    .systemBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = stringResource(id = R.string.lets_meet),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.headline
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.lets_meet_desc),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )

                Spacer(modifier = Modifier.height(20.dp))

                YallaTextField(
                    text = uiState.firstName,
                    onChangeText = { onIntent(CredentialsIntent.SetFirstName(it)) },
                    placeHolderText = stringResource(id = R.string.name),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(10.dp))


                YallaTextField(
                    text = uiState.lastName,
                    onChangeText = { onIntent(CredentialsIntent.SetLastName(it)) },
                    placeHolderText = stringResource(id = R.string.surname)
                )


                Spacer(modifier = Modifier.height(10.dp))


                YallaTextField(
                    text = uiState.dateOfBirth?.formatWithDotsDMY() ?: "",
                    placeHolderText = stringResource(id = R.string.date_of_birth),
                    onChangeText = {},
                    trailingIcon = painterResource(id = R.drawable.ic_calendar),
                    onClick = { onIntent(CredentialsIntent.OpenDateBottomSheet) }
                )


                Spacer(modifier = Modifier.height(10.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GenderButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.gender_m),
                        isSelected = uiState.gender == Gender.MALE,
                        onSelect = { onIntent(CredentialsIntent.SetGender(Gender.MALE)) }
                    )

                    GenderButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.gender_f),
                        isSelected = uiState.gender == Gender.FEMALE,
                        onSelect = { onIntent(CredentialsIntent.SetGender(Gender.FEMALE)) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = uiState.firstName.isNotBlank() && uiState.lastName.isNotBlank() && uiState.gender != Gender.NOT_SELECTED && uiState.dateOfBirth != null,
                    onClick = { onIntent(CredentialsIntent.Register) }
                )
            }

            AnimatedVisibility(
                visible = sheetState.bottomSheetState.isVisible,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(targetAlpha = 1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            onClick = { onIntent(CredentialsIntent.CloseDateBottomSheet) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    )
}