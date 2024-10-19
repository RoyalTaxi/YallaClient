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
import androidx.compose.foundation.lazy.LazyColumn
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
import uz.ildam.technologies.yalla.android.utils.Utils.formatWithDashesDMY


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CredentialsScreen(
    firstName: String,
    lastName: String,
    dateOfBirth: LocalDate?,
    gender: String,
    state: BottomSheetScaffoldState,
    onClickDate: () -> Unit,
    onUpdateFirstName: (String) -> Unit,
    onUpdateLastName: (String) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onUpdateGender: (String) -> Unit,
    onDismissRequestBottomSheet: () -> Unit,
    onBack: () -> Unit,
    onRegister: () -> Unit,
) {

    BackHandler(onBack = onBack)

    BottomSheetScaffold(
        sheetDragHandle = null,
        sheetSwipeEnabled = false,
        scaffoldState = state,
        containerColor = YallaTheme.color.white,
        sheetContainerColor = YallaTheme.color.gray2,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            DatePickerBottomSheet(
                startDate = dateOfBirth ?: LocalDate.now(),
                onSelectDate = onSelectDate,
                onDismissRequest = onDismissRequestBottomSheet
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
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item { Spacer(modifier = Modifier.height(80.dp)) }

                    item {
                        Text(
                            text = stringResource(id = R.string.lets_meet),
                            color = YallaTheme.color.black,
                            style = YallaTheme.font.headline
                        )
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        Text(
                            text = stringResource(id = R.string.lets_meet_desc),
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.body
                        )
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        YallaTextField(
                            text = firstName,
                            onChangeText = onUpdateFirstName,
                            placeHolderText = stringResource(id = R.string.name),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    item {
                        YallaTextField(
                            text = lastName,
                            onChangeText = onUpdateLastName,
                            placeHolderText = stringResource(id = R.string.surname)
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    item {
                        YallaTextField(
                            text = dateOfBirth?.formatWithDashesDMY() ?: "",
                            placeHolderText = stringResource(id = R.string.date_of_birth),
                            onChangeText = {},
                            trailingIcon = painterResource(id = R.drawable.ic_calendar),
                            onClick = onClickDate
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GenderButton(
                                modifier = Modifier.weight(1f),
                                text = stringResource(id = R.string.gender_m),
                                isSelected = gender == "MALE",
                                onSelect = { onUpdateGender("MALE") }
                            )

                            GenderButton(
                                modifier = Modifier.weight(1f),
                                text = stringResource(id = R.string.gender_f),
                                isSelected = gender == "FEMALE",
                                onSelect = { onUpdateGender("FEMALE") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                YallaButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.next),
                    enabled = firstName.isNotBlank() && lastName.isNotBlank() && gender != "NOT_SELECTED" && dateOfBirth != null,
                    onClick = onRegister
                )
            }

            AnimatedVisibility(
                visible = state.bottomSheetState.isVisible,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(targetAlpha = 1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            onClick = onDismissRequestBottomSheet,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    )
}