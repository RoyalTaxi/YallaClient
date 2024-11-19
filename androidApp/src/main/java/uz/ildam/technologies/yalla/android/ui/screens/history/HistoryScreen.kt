package uz.ildam.technologies.yalla.android.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.HistoryOrderItem
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    orders: LazyPagingItems<OrderHistory>,
    onIntent: (HistoryIntent) -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = YallaTheme.color.white,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YallaTheme.color.white,
                    scrolledContainerColor = YallaTheme.color.white
                ),
                title = {
                    Text(
                        text = stringResource(R.string.orders_history),
                        color = YallaTheme.color.black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(HistoryIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            when (orders.loadState.refresh) {
                is LoadState.Loading -> LoadingDialog()
                is LoadState.NotLoading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(orders.itemCount) { index ->
                            orders[index]?.let { order ->
                                when (order) {
                                    is OrderHistory.Date -> {
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

                                    is OrderHistory.Item -> {
                                        HistoryOrderItem(order = order)
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {

                }
            }
        }
    )
}

private fun getRelativeDate(date: String, today: String, yesterday: String): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val orderDate = try {
        val (day, month, year) = date.split(".").map { it.toInt() }
        LocalDate(year, month, day)
    } catch (e: Exception) {
        return date
    }

    return when (orderDate) {
        currentDate -> today
        currentDate.minus(1, DateTimeUnit.DAY) -> yesterday
        else -> "${orderDate.dayOfMonth}.${orderDate.month.number}.${orderDate.year}"
    }
}