package uz.yalla.client.feature.android.payment.corporate_account.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.corporate_account.components.BusinessAccountTextField
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountUIState
import uz.yalla.client.feature.android.payment.corporate_account.view.CorporateAccountIntent
import uz.yalla.client.feature.core.components.text_field.LoginNumberField
import uz.yalla.client.feature.core.components.text_field.YTextField
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
internal fun AddCompanyPage(
    uiState: CorporateAccountUIState,
    onIntent: (CorporateAccountIntent) -> Unit,
) {

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {

        Text(
            text = stringResource(R.string.add_company),
            color = YallaTheme.color.black,
            style = YallaTheme.font.headline,
        )

        Spacer(modifier = Modifier.height(10.dp))

        BusinessAccountTextField(
            text = uiState.name,
            onChangeText = { onIntent(CorporateAccountIntent.SetCompanyName(it)) },
            placeHolderText = stringResource(id = R.string.company_name),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.city,
            onChangeText = { onIntent(CorporateAccountIntent.SetCity(it)) },
            placeHolderText = stringResource(id = R.string.city),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.contactPerson,
            onChangeText = { onIntent(CorporateAccountIntent.SetPerson(it)) },
            placeHolderText = stringResource(id = R.string.contact_person),
            modifier = Modifier.fillMaxWidth()
        )

        LoginNumberField(
            number = uiState.number,
            onUpdateNumber = { number -> onIntent(CorporateAccountIntent.SetNumber(number)) }
        )

        BusinessAccountTextField(
            text = uiState.email,
            onChangeText = { onIntent(CorporateAccountIntent.SetEmail(it)) },
            placeHolderText = stringResource(id = R.string.email),
            modifier = Modifier.fillMaxWidth()
        )
    }
}