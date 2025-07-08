package uz.yalla.client.feature.payment.top_up_balance.view

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.top_up_balance.components.input.BalanceInputField
import uz.yalla.client.feature.payment.top_up_balance.model.TopUpUIState

@Composable
internal fun TopUpScreen(
    onIntent: (TopUpIntent) -> Unit,
    uiState: TopUpUIState
) {
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        containerColor = YallaTheme.color.background,
        modifier = Modifier.imePadding(),
        topBar = { TopUpAppBar { onIntent(TopUpIntent.OnNavigateBack) } },
        content = { paddingValues ->
            TopUpContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onValueChange = { value -> onIntent(TopUpIntent.SetValue(value)) },
                focusRequester = focusRequester
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopUpAppBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        title = {
            Text(
                text = stringResource(R.string.top_up_balanse),
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
private fun TopUpContent(
    modifier: Modifier,
    uiState: TopUpUIState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        BalanceInputField(
            balance = uiState.topUpAmount,
            onBalanceChange = onValueChange,
            focusRequester = focusRequester
        )

        Text(
            text = stringResource(R.string.min_value),
            color = YallaTheme.color.gray,
            style = YallaTheme.font.body
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.pay),
            enabled = uiState.isPayButtonValid,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}