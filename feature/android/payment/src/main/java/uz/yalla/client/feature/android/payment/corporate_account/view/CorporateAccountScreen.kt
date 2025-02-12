package uz.yalla.client.feature.android.payment.corporate_account.view

import android.graphics.pdf.PdfDocument.Page
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.corporate_account.model.AddCompanyUiState
import uz.yalla.client.feature.core.components.text_field.LoginNumberField
import uz.yalla.client.feature.core.components.text_field.YTextField
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddCompanyScreen(
    uiState: AddCompanyUiState,
    onIntent: (AddCompanyIntent) -> Unit,
    pagerState: PagerState,
    screenContents: List<Page>
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {}
            )
        },
        content = {paddingValues ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.padding(paddingValues))

                Text(
                    text = stringResource(R.string.add_company),
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.headline,
                )

                Spacer(modifier = Modifier.height(20.dp))

                YTextField(
                    text = uiState.name,
                    onChangeText = { onIntent(AddCompanyIntent.setCompanyName(it)) },
                    placeHolderText = stringResource(id = R.string.company_name),
                    modifier = Modifier.fillMaxWidth()
                )

                YTextField(
                    text = uiState.city,
                    onChangeText = { onIntent(AddCompanyIntent.setCity(it)) },
                    placeHolderText = stringResource(id = R.string.city),
                    modifier = Modifier.fillMaxWidth()
                )

                YTextField(
                    text = uiState.contactPerson,
                    onChangeText = { onIntent(AddCompanyIntent.setPersen(it)) },
                    placeHolderText = stringResource(id = R.string.contact_person),
                    modifier = Modifier.fillMaxWidth()
                )

                LoginNumberField(
                    number = uiState.number,
                    onUpdateNumber = { number -> onIntent(AddCompanyIntent.setNumber(number)) }
                )

                YTextField(
                    text = uiState.email,
                    onChangeText = { onIntent(AddCompanyIntent.setEmail(it)) },
                    placeHolderText = stringResource(id = R.string.email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}