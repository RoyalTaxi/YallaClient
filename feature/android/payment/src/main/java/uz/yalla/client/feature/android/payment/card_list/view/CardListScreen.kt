 package uz.yalla.client.feature.android.payment.card_list.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.card_list.model.CardListUIState
import uz.yalla.client.feature.core.components.items.SelectPaymentTypeItem
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CardListScreen(
    uiState: CardListUIState,
    onNavigateBack: () -> Unit,
    onSelectItem: (PaymentType) -> Unit,
    onIntent: (CardListIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YallaTheme.color.white)
                    .padding(paddingValues)
            ) {

                item { Spacer(modifier = Modifier.height(40.dp)) }

                item {
                    Text(
                        text = stringResource(id = R.string.payment_method),
                        color = YallaTheme.color.black,
                        style = YallaTheme.font.headline,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    Text(
                        text = stringResource(id = R.string.choose_payment_method),
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.body,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    SelectPaymentTypeItem(
                        isSelected = uiState.selectedPaymentType == PaymentType.CASH,
                        tint = YallaTheme.color.gray,
                        text = stringResource(R.string.cash),
                        painter = painterResource(R.drawable.ic_money),
                        onSelect = { onSelectItem(PaymentType.CASH) }
                    )
                }

                items(uiState.cards) { cardListItem ->
                    SelectPaymentTypeItem(
                        isSelected = uiState.selectedPaymentType == PaymentType.CARD(
                            cardListItem.cardId,
                            cardListItem.maskedPan
                        ),
                        painter = painterResource(
                            id = when (cardListItem.cardId.length) {
                                16 -> R.drawable.img_logo_humo
                                32 -> R.drawable.img_logo_uzcard
                                else -> R.drawable.ic_money
                            }
                        ),
                        text = cardListItem.maskedPan,
                        onSelect = {
                            onSelectItem(
                                PaymentType.CARD(
                                    cardListItem.cardId,
                                    cardListItem.maskedPan
                                )
                            )
                        }
                    )
                }

                item {
                    SelectPaymentTypeItem(
                        isSelected = false,
                        tint = YallaTheme.color.gray,
                        painter = painterResource(R.drawable.ic_add),
                        text = stringResource(R.string.add_card),
                        onSelect = { onIntent(CardListIntent.AddNewCard) },
                    )

//                    SelectPaymentTypeItem(
//                        isSelected = false,
//                        tint = YallaTheme.color.gray,
//                        painter = painterResource(R.drawable.ic_add),
//                        text = stringResource(R.string.add_corporate_account),
//                        onSelect = { onIntent(CardListIntent.AddCorporateAccount) },
//                    )
//
//                    SelectPaymentTypeItem(
//                        isSelected = false,
//                        tint = YallaTheme.color.gray,
//                        painter = painterResource(R.drawable.ic_add),
//                        text = stringResource(R.string.business_account),
//                        onSelect = { onIntent(CardListIntent.AddBusinessAccount) },
//                    )
                }
            }
        }
    )
}