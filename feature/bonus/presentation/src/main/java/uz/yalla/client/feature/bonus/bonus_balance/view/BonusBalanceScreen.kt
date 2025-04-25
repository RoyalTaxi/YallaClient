package uz.yalla.client.feature.bonus.bonus_balance.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.presentation.R

@Composable
internal fun BonusBalanceScreen(
    onIntent: (BonusBalanceIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = { BonusBalanceTopBar { onIntent(BonusBalanceIntent.OnNavigateBack) } },
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.white)
            .navigationBarsPadding(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.bonus),
                    style = YallaTheme.font.title2,
                    color = YallaTheme.color.black,
                    modifier = Modifier.padding(vertical = 17.dp)
                )

                BonusBalanceItem(
                    percentage = "3",
                    iconId = R.drawable.img_default_car,
                    body = stringResource(R.string.bonus_body),
                    onClick = {}
                )

                Text(
                    text = stringResource(R.string.can_pay_with_bonus),
                    style = YallaTheme.font.title2,
                    color = YallaTheme.color.black,
                    modifier = Modifier.padding(vertical = 17.dp)
                )

                BonusBalanceItem(
                    percentage = "50",
                    iconId = R.drawable.ic_coin,
                    body = stringResource(R.string.second_bonus_body),
                    backgroundColor = YallaTheme.color.primary,
                    textColor = YallaTheme.color.white,
                    bodyTextColor = YallaTheme.color.white,
                    onClick = {}
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BonusBalanceTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        title = {
            Text(
                text = stringResource(R.string.bonus_balance),
                style = YallaTheme.font.labelLarge,
                color = YallaTheme.color.black,
                textAlign = TextAlign.Center
            )
        },
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

@Composable
private fun BonusBalanceItem(
    percentage: String,
    iconId: Int,
    body: String,
    onClick: () -> Unit,
    backgroundColor: Color = YallaTheme.color.gray2,
    textColor: Color = YallaTheme.color.black,
    bodyTextColor: Color = YallaTheme.color.gray,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.percentage, percentage),
                    style = YallaTheme.font.headline,
                    color = textColor
                )

                Image(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.height(30.dp)
                )
            }

            Text(
                text = body,
                style = YallaTheme.font.body,
                color = bodyTextColor
            )
        }
    }
}