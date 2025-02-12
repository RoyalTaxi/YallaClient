package uz.yalla.client.feature.android.payment.top_up_balance.view

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
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.top_up_balance.model.TopUpUIState
import uz.yalla.client.feature.android.payment.top_up_balance.components.input.BalanceInputField
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopUpScreen(
    onIntent: (TopUpIntent) -> Unit,
    uiState: TopUpUIState
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                title = {
                    Text(
                        text = stringResource(R.string.top_up_balanse),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.labelLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(TopUpIntent.OnNavigateBack) }) {
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
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                BalanceInputField(
                    balance = uiState.topUpAmount,
                    onBalanceChange = { value -> onIntent(TopUpIntent.SetValue(value)) }
                )

                Text(
                    text = stringResource(R.string.min_value),
                    color = YallaTheme.color.gray,
                    style = YallaTheme.font.body
                )

                Spacer(modifier = Modifier.weight(1f))

                YButton(
                    text = stringResource(R.string.pay),
                    enabled = uiState.isPayButtonValid,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
