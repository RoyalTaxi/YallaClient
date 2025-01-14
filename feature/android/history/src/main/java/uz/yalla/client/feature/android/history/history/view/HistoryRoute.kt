package uz.yalla.client.feature.android.history.history.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.android.history.history.model.HistoryViewModel

@Composable
internal fun HistoryRoute(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {

    val orders = vm.orders.collectAsLazyPagingItems()

    LaunchedEffect(Unit) { vm.getOrders() }

    HistoryScreen(
        orders = orders,
        onIntent = { intent ->
            when (intent) {
                is HistoryIntent.OnHistoryItemClick -> onClickItem(intent.id.toInt())
                is HistoryIntent.OnNavigateBack -> onBack()
            }
        }
    )
}