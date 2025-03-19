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
import uz.yalla.client.feature.payment.corporate_account.components.BusinessAccountTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEmployeeScreen(
    onIntent: (AddEmployeeIntent) -> Unit,
    uiState: AddEmployeeUIState
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(R.string.add_employee),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {onIntent(AddEmployeeIntent.OnNavigateBack)}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                PhoneNumberField(
                    number = uiState.number,
                    onUpdateNumber = { number -> onIntent(AddEmployeeIntent.SetNumber(number)) }
                )

                BusinessAccountTextField(
                    text = uiState.fullName,
                    onChangeText = { onIntent(AddEmployeeIntent.SetFullName(it)) },
                    placeHolderText = stringResource(id = R.string.full_name),
                )


                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(R.string.add),
                    enabled = uiState.isAddButtonValid,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}