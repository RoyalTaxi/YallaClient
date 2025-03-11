package uz.yalla.client.feature.android.payment.corporate_account.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.corporate_account.components.BusinessAccountTextField
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountUIState
import uz.yalla.client.feature.android.payment.corporate_account.view.CorporateAccountIntent

@Composable
internal fun AddLegalAddressPage(
    uiState: CorporateAccountUIState,
    onIntent: (CorporateAccountIntent) -> Unit,
) {

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.legal_address),
            color = YallaTheme.color.black,
            style = YallaTheme.font.headline,
        )

        Spacer(modifier = Modifier.height(10.dp))

        BusinessAccountTextField(
            text = uiState.index,
            onChangeText = { onIntent(CorporateAccountIntent.SetIndex(it)) },
            placeHolderText = stringResource(id = R.string.index),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.street,
            onChangeText = { onIntent(CorporateAccountIntent.SetStreet(it)) },
            placeHolderText = stringResource(id = R.string.street),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.homeOffice,
            onChangeText = { onIntent(CorporateAccountIntent.SetHomeOffice(it)) },
            placeHolderText = stringResource(id = R.string.home_office),
            modifier = Modifier.fillMaxWidth()
        )
    }
}