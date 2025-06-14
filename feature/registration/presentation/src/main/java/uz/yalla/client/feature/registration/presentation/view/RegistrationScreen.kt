package uz.yalla.client.feature.registration.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate
import uz.yalla.client.core.common.button.GenderButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PrimaryTextField
import uz.yalla.client.core.common.formation.formatWithDotsDMY
import uz.yalla.client.core.common.sheet.DatePickerModalBottomSheet
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.registration.presentation.R
import uz.yalla.client.feature.registration.presentation.model.Gender
import uz.yalla.client.feature.registration.presentation.model.RegistrationUIState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegistrationScreen(
    uiState: RegistrationUIState,
    onIntent: (RegistrationIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    RegistrationContent(
        uiState = uiState,
        sheetState = sheetState,
        onIntent = onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent(
    uiState: RegistrationUIState,
    sheetState: SheetState,
    onIntent: (RegistrationIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .padding(20.dp)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        RegistrationHeader()

        Spacer(modifier = Modifier.height(20.dp))

        RegistrationForm(
            uiState = uiState,
            onIntent = onIntent
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(20.dp))

        RegistrationButton(
            isEnabled = uiState.isFormValid(),
            onClick = { onIntent(RegistrationIntent.Register) }
        )

        if (uiState.isDatePickerVisible) {
            DatePickerModalBottomSheet(
                sheetState = sheetState,
                startDate = uiState.dateOfBirth ?: LocalDate.now(),
                onSelectDate = { onIntent(RegistrationIntent.SetDateOfBirth(it)) },
                onDismissRequest = { onIntent(RegistrationIntent.CloseDateBottomSheet) }
            )
        }
    }
}

@Composable
private fun RegistrationHeader() {
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
}

@Composable
private fun RegistrationForm(
    uiState: RegistrationUIState,
    onIntent: (RegistrationIntent) -> Unit
) {
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

    GenderSelection(
        selectedGender = uiState.gender,
        onGenderSelect = { onIntent(RegistrationIntent.SetGender(it)) }
    )
}

@Composable
private fun GenderSelection(
    selectedGender: Gender,
    onGenderSelect: (Gender) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_m),
            isSelected = selectedGender == Gender.MALE,
            onSelect = { onGenderSelect(Gender.MALE) }
        )

        GenderButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.gender_f),
            isSelected = selectedGender == Gender.FEMALE,
            onSelect = { onGenderSelect(Gender.FEMALE) }
        )
    }
}

@Composable
private fun RegistrationButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.next),
        enabled = isEnabled,
        onClick = onClick
    )
}

private fun RegistrationUIState.isFormValid(): Boolean {
    return firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            gender != Gender.NOT_SELECTED &&
            dateOfBirth != null
}