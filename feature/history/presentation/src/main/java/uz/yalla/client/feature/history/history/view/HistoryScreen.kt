package uz.yalla.client.feature.history.history.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import uz.yalla.client.core.common.topbar.CenterAlignedScrollableTopBar
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.OrdersHistory
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history.components.OrderHistoryItem
import uz.yalla.client.core.common.utils.getRelativeDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    orders: LazyPagingItems<OrdersHistory>,
    onIntent: (HistoryIntent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = YallaTheme.color.white,
        topBar = {
            CenterAlignedScrollableTopBar(
                title = stringResource(R.string.orders_history),
                collapsedTitleTextStyle = YallaTheme.font.labelLarge,
                expandedTitleTextStyle = YallaTheme.font.headline,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YallaTheme.color.white,
                    scrolledContainerColor = YallaTheme.color.white
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(HistoryIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders.itemCount) { index ->
                orders[index]?.let { order ->
                    when (order) {
                        is OrdersHistory.Date -> {
                            Text(
                                text = getRelativeDate(
                                    date = order.date,
                                    today = stringResource(R.string.today),
                                    yesterday = stringResource(R.string.yesterday)
                                ),
                                color = YallaTheme.color.black,
                                style = YallaTheme.font.title2,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        is OrdersHistory.Item -> {
                            OrderHistoryItem(
                                order = order,
                                onClick = { onIntent(HistoryIntent.OnHistoryItemClick(order.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}