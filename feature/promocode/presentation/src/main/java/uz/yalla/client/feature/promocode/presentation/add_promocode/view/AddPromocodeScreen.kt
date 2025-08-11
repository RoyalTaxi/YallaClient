package uz.yalla.client.feature.promocode.presentation.add_promocode.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.promocode.presentation.R
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeIntent
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeState
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.PromocodeButtonState

@Composable
fun AddPromocodeScreen(
    state: AddPromocodeState,
    focusRequester: FocusRequester,
    onIntent: (AddPromocodeIntent) -> Unit
) {
    when (state) {
        is AddPromocodeState.Idle -> AddPromocodeIdleScreen(state, focusRequester, onIntent)
        is AddPromocodeState.Error -> AddPromocodeErrorScreen(state, onIntent)
        is AddPromocodeState.Success -> AddPromocodeSuccessScreen(state, onIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPromocodeIdleScreen(
    state: AddPromocodeState.Idle,
    focusRequester: FocusRequester,
    onIntent: (AddPromocodeIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddPromocodeIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = YallaTheme.color.onBackground
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.promocode),
                        style = YallaTheme.font.labelLarge,
                        color = YallaTheme.color.onBackground
                    )
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = state.promoCode,
                    onValueChange = { onIntent(AddPromocodeIntent.UpdatePromoCode(it)) },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.add_promocode),
                            style = YallaTheme.font.title2,
                            color = YallaTheme.color.gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = YallaTheme.font.title2
                        .copy(color = YallaTheme.color.onBackground)
                        .copy(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,

                        focusedContainerColor = YallaTheme.color.background,
                        unfocusedContainerColor = YallaTheme.color.background,
                        errorContainerColor = YallaTheme.color.background,
                        disabledContainerColor = YallaTheme.color.background,

                        cursorColor = YallaTheme.color.onBackground,
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                )
            }
        },
        bottomBar = {
            PrimaryButton(
                text = stringResource(R.string.add_promocode),
                onClick = { onIntent(AddPromocodeIntent.ActivatePromocode) },
                enabled = state.buttonState == PromocodeButtonState.ADD_ENABLED,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            )
        }
    )
}

@Composable
private fun AddPromocodeSuccessScreen(
    state: AddPromocodeState.Success,
    onIntent: (AddPromocodeIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.background)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_success),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Text(
                text = stringResource(R.string.x_bonus_amount, state.data.amount),
                style = YallaTheme.font.title2,
                color = YallaTheme.color.onBackground
            )

            Text(
                text = stringResource(R.string.already_in_balance),
                style = YallaTheme.font.body,
                color = YallaTheme.color.gray
            )
        }

        PrimaryButton(
            text = stringResource(R.string.great),
            onClick = { onIntent(AddPromocodeIntent.NavigateBack) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun AddPromocodeErrorScreen(
    state: AddPromocodeState.Error,
    onIntent: (AddPromocodeIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.background)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Text(
                text = stringResource(R.string.incorrect_code),
                style = YallaTheme.font.title2,
                color = YallaTheme.color.onBackground
            )

            state.message.takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = state.message,
                    style = YallaTheme.font.body,
                    color = YallaTheme.color.gray
                )
            }
        }

        PrimaryButton(
            text = stringResource(R.string.retry),
            onClick = { onIntent(AddPromocodeIntent.RetryAgain) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}