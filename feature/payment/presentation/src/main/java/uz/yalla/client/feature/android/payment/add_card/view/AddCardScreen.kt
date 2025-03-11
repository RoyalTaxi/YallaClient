package uz.yalla.client.feature.android.payment.add_card.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.add_card.components.CardViewCard
import uz.yalla.client.feature.android.payment.add_card.model.AddCardUIState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddCardScreen(
    uiState: AddCardUIState,
    onIntent: (AddCardIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(R.string.new_card),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddCardIntent.OnNavigateBack) }) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                CardViewCard(
                    cardNumber = uiState.cardNumber,
                    cardDate = uiState.cardExpiry,
                    onCardNumberChange = { onIntent(AddCardIntent.SetCardNumber(it)) },
                    onCardDateChange = { onIntent(AddCardIntent.SetExpiryDate(it)) },
                    onClickCamera = { onIntent(AddCardIntent.OnClickScanCard) }
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton (
                    text = stringResource(R.string.link_card),
                    enabled = uiState.buttonState,
                    onClick = { onIntent(AddCardIntent.OnClickLinkCard) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}