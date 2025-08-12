package uz.yalla.client.feature.payment.add_employee.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.field.PhoneNumberField
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.add_employee.model.AddEmployeeUIState
import uz.yalla.client.feature.payment.corporate_account.components.BusinessAccountTextField

@Composable
 fun AddEmployeeScreen(
    onIntent: (AddEmployeeIntent) -> Unit,
    uiState: AddEmployeeUIState
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        modifier = Modifier.imePadding(),
        topBar = { AddEmployeeTopBar { onIntent(AddEmployeeIntent.OnNavigateBack) }},
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                AddEmployeeContent(
                    uiState = uiState,
                    onIntent = onIntent
                )

                Spacer(modifier = Modifier.weight(1f))

                AddEmployeeFooter(
                    isAddButtonValid = uiState.isAddButtonValid,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEmployeeTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        title = {
            Text(
                text = stringResource(R.string.add_employee),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.labelLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        }
    )
}

@Composable
private fun AddEmployeeContent(
    uiState: AddEmployeeUIState,
    onIntent: (AddEmployeeIntent) -> Unit
) {
    PhoneNumberField(
        number = uiState.number,
        onUpdateNumber = { onIntent(AddEmployeeIntent.SetNumber(it)) }
    )

    BusinessAccountTextField(
        text = uiState.fullName,
        onChangeText = { onIntent(AddEmployeeIntent.SetFullName(it)) },
        placeHolderText = stringResource(id = R.string.full_name),
    )
}

@Composable
fun AddEmployeeFooter(
    isAddButtonValid: Boolean,
) {
    PrimaryButton(
        text = stringResource(R.string.add),
        enabled = isAddButtonValid,
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    )
}