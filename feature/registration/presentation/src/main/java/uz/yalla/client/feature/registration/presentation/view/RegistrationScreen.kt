package uz.yalla.client.feature.registration.presentation.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import uz.yalla.client.core.common.button.GenderButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PrimaryTextField
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.sheet.DatePickerBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.registration.presentation.R
import uz.yalla.client.feature.registration.presentation.model.RegistrationUIState
import uz.yalla.client.feature.registration.presentation.model.Gender


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegistrationScreen(
    sheetState: BottomSheetScaffoldState,
    uiState: RegistrationUIState,
    onIntent: (RegistrationIntent) -> Unit
) {

    BackHandler(onBack = { onIntent(RegistrationIntent.NavigateBack) })

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
                onSelectDate = { onIntent(RegistrationIntent.SetDateOfBirth(it)) },
                onDismissRequest = { onIntent(RegistrationIntent.CloseDateBottomSheet) }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YallaTheme.color.white)
                    .padding(20.dp)
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
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

                PrimaryTextField(
                    text = uiState.firstName,
                    onChangeText = { onIntent(RegistrationIntent.SetFirstName(it)) },
                    placeHolderText = stringResource(id = R.string.name),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(10.dp))


                PrimaryTextField(
                    text = uiState.lastName,
                    onChangeText = { onIntent(RegistrationIntent.SetLastName(it)) },
                    placeHolderText = stringResource(id = R.string.surname)
                )


                Spacer(modifier = Modifier.height(10.dp))


                PrimaryTextField(
                    text = uiState.dateOfBirth?.formatWithDotsDMY() ?: "",
                    placeHolderText = stringResource(id = R.string.date_of_birth),
                    onChangeText = {},
                    trailingIcon = painterResource(id = R.drawable.ic_calendar),
                    onClick = { onIntent(RegistrationIntent.OpenDateBottomSheet) }
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
                        onSelect = { onIntent(RegistrationIntent.SetGender(Gender.MALE)) }
                    )

                    GenderButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.gender_f),
                        isSelected = uiState.gender == Gender.FEMALE,
                        onSelect = { onIntent(RegistrationIntent.SetGender(Gender.FEMALE)) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = uiState.firstName.isNotBlank() && uiState.lastName.isNotBlank() && uiState.gender != Gender.NOT_SELECTED && uiState.dateOfBirth != null,
                    onClick = { onIntent(RegistrationIntent.Register) }
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
                            onClick = { onIntent(RegistrationIntent.CloseDateBottomSheet) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    )
}