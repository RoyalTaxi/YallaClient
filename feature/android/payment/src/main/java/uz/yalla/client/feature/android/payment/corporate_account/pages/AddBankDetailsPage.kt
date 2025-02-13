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
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.corporate_account.components.BusinessAccountTextField
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountUIState
import uz.yalla.client.feature.android.payment.corporate_account.view.CorporateAccountIntent
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
internal fun AddBankDetailsPage(
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
            text = stringResource(R.string.bank_details),
            color = YallaTheme.color.black,
            style = YallaTheme.font.headline,
        )

        Spacer(modifier = Modifier.height(10.dp))

        BusinessAccountTextField(
            text = uiState.bankName,
            onChangeText = { onIntent(CorporateAccountIntent.SetBankName(it)) },
            placeHolderText = stringResource(id = R.string.bank_name),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.currentAccount,
            onChangeText = { onIntent(CorporateAccountIntent.SetCurrentAccount(it)) },
            placeHolderText = stringResource(id = R.string.current_account),
            modifier = Modifier.fillMaxWidth()
        )

        BusinessAccountTextField(
            text = uiState.mfo,
            onChangeText = { onIntent(CorporateAccountIntent.SetMFO(it)) },
            placeHolderText = stringResource(id = R.string.mfo),
            modifier = Modifier.fillMaxWidth()
        )
    }
}