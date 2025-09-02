package uz.yalla.client.feature.bonus.bonus_account.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import uz.yalla.client.core.common.item.BonusBalanceItem
import uz.yalla.client.core.common.item.OrderOptionsItem
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.bonus.presentation.R

@Composable
internal fun BonusAccountScreen(
    balance: Long,
    onIntent: (BonusAccountIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = {
            BonusAccountTopBar {
                onIntent(BonusAccountIntent.OnNavigateBack)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = YallaTheme.color.background)
            .navigationBarsPadding(),
        content = { paddingValues ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                stickyHeader {
                    Spacer(modifier = Modifier.height(40.dp))

                    BonusAccountHeader()
                }

                item {
                    BonusBalanceItem(
                        balance = balance.toString(),
                        onClickBalance = { }
                    )
                }

                item {
                    PromoCodes {
                        onIntent(BonusAccountIntent.NavigateToAddPromocode)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BonusAccountTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        title = {},
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
private fun BonusAccountHeader() {
    Text(
        text = stringResource(R.string.bonus_promocode),
        color = YallaTheme.color.onBackground,
        style = YallaTheme.font.headline
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.bonus_definition),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body
    )
}

@Composable
private fun PromoCodes(
    onEnterPromoCode: () -> Unit
) {
    Text(
        text = stringResource(R.string.promocodes),
        style = YallaTheme.font.title2,
        color = YallaTheme.color.onBackground,
        modifier = Modifier.padding(vertical = 17.dp)
    )

    OrderOptionsItem(
        title = stringResource(R.string.enter_promocod),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_coupon),
                contentDescription = null,
                tint = YallaTheme.color.onBackground
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