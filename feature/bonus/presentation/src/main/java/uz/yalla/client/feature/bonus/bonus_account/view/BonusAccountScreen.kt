package uz.yalla.client.feature.bonus.bonus_account.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.item.OrderOptionsItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.presentation.R

@Composable
internal fun BonusAccountScreen(
    onIntent: ( BonusAccountIntent ) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = { BonusAccountTopBar { onIntent(BonusAccountIntent.OnNavigateBack) }},
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .navigationBarsPadding(),
        content = {paddingValues ->
            BonusAccountContent(
                modifier = Modifier.padding(paddingValues),
                bonusBalance = "96500",
                onClickPromoCode = {},
                onClickBalance = {
                    onIntent(BonusAccountIntent.OnBonusClicked)
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BonusAccountTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BonusAccountContent(
    modifier: Modifier,
    bonusBalance: String,
    onClickBalance: () -> Unit,
    onClickPromoCode: () -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        stickyHeader {
            Spacer(modifier = Modifier.height(40.dp))

            BonusAccountHeader()

        }

        item {
            BonusBalance(
                balance = bonusBalance,
                onClickBalance = onClickBalance
            )
        }
        
        item { 
            PromoCods (
                onEnterPromoCode = onClickPromoCode
            )
        }
    }
}

@Composable
private fun BonusAccountHeader() {
    Text(
        text = stringResource(R.string.bonus_promocode),
        color = YallaTheme.color.black,
        style = YallaTheme.font.headline,
        modifier = Modifier.padding(start = 20.dp, end = 60.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.bonus_definition),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body,
        modifier = Modifier.padding(start = 20.dp, end = 60.dp)
    )
}

@Composable
private fun BonusBalance(
    balance: String,
    onClickBalance: () -> Unit
) {
    Card(
        onClick = onClickBalance,
        colors = CardDefaults.cardColors(YallaTheme.color.primary),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.bonus_balance),
                    style = YallaTheme.font.label,
                    color = YallaTheme.color.white
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = balance,
                        style = YallaTheme.font.title,
                        color = YallaTheme.color.white
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_coin),
                        contentDescription = null,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = YallaTheme.color.white
            )
        }
    }
}

@Composable
private fun PromoCods(
    onEnterPromoCode: () -> Unit
) {

    Column {
        Text(
            text = stringResource(R.string.promocodes),
            style = YallaTheme.font.title2,
            color = YallaTheme.color.black,
            modifier = Modifier.padding(vertical = 17.dp, horizontal = 20.dp)
        )

        OrderOptionsItem(
            title = stringResource(R.string.enter_promocod),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_coupon),
                    contentDescription = null,
                    tint = YallaTheme.color.black
                )
                Spacer(modifier = Modifier.width(16.dp))
            },
            trailingIcon = {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = YallaTheme.color.gray
                )
            },
            onClick = onEnterPromoCode
        )
    }
}