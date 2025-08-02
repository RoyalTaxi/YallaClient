package uz.yalla.client.feature.payment.card_list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import uz.yalla.client.core.common.item.SelectPaymentTypeItem
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R
import uz.yalla.client.feature.payment.card_list.components.CardListItem
import uz.yalla.client.feature.payment.card_list.model.CardListUIState

@Composable
internal fun CardListScreen(
    uiState: CardListUIState,
    onNavigateBack: () -> Unit,
    onSelectItem: (PaymentType) -> Unit,
    onIntent: (CardListIntent) -> Unit
) {
    Scaffold(
        topBar = { CardListTopBar(onNavigateBack) },
        content = { paddingValues ->
            CardListContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YallaTheme.color.background)
                    .padding(paddingValues),
                uiState = uiState,
                onSelectItem = onSelectItem,
                onIntent = onIntent
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardListTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.background),
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
private fun CardListContent(
    modifier: Modifier = Modifier,
    uiState: CardListUIState,
    onSelectItem: (PaymentType) -> Unit,
    onIntent: (CardListIntent) -> Unit
) {
    val prefs = koinInject<AppPreferences>()
    val cardEnabled by prefs.isCardEnabled.collectAsStateWithLifecycle(false)
    LazyColumn(modifier = modifier) {
        item { Spacer(modifier = Modifier.height(40.dp)) }

        item { CardListHeader() }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            SelectPaymentTypeItem(
                isSelected = uiState.selectedPaymentType == PaymentType.CASH,
                text = stringResource(R.string.cash),
                painter = painterResource(R.drawable.ic_money_color),
                onSelect = { onSelectItem(PaymentType.CASH) }
            )
        }

        items(uiState.cards) { cardListItem ->
            CardListItem(
                enabled = cardEnabled,
                isSelected = uiState.selectedPaymentType == PaymentType.CARD(
                    cardListItem.cardId,
                    cardListItem.maskedPan
                ),
                painter = painterResource(
                    id = getCardLogo(cardListItem.cardId)
                ),

                text = cardListItem.maskedPan,
                onSelect = {
                    onSelectItem(
                        PaymentType.CARD(
                            cardListItem.cardId,
                            cardListItem.maskedPan
                        )
                    )
                },
                onDelete = {
                    onIntent(CardListIntent.OnDeleteCard(cardId = cardListItem.cardId))
                }
            )
        }

        if (cardEnabled) item {
            SelectPaymentTypeItem(
                isSelected = false,
                tint = YallaTheme.color.gray,
                painter = painterResource(R.drawable.ic_add),
                text = stringResource(R.string.add_card),
                onSelect = { onIntent(CardListIntent.AddNewCard) }
            )
        }
    }
}

@Composable
private fun CardListHeader() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = stringResource(id = R.string.payment_method),
            color = YallaTheme.color.onBackground,
            style = YallaTheme.font.headline
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.choose_payment_method),
            color = YallaTheme.color.gray,
            style = YallaTheme.font.body
        )
    }
}

private fun getCardLogo(cardId: String): Int {
    return when (cardId.length) {
        16 -> R.drawable.ic_humo
        32 -> R.drawable.ic_uzcard
        else -> R.drawable.ic_money
    }
}