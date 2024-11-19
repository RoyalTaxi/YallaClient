package uz.ildam.technologies.yalla.android.ui.screens.history

import androidx.compose.runtime.Composable
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {

    val orders = vm.orders.collectAsLazyPagingItems()

    HistoryScreen(
        orders = orders,
        onIntent = { intent ->
            when (intent) {
                is HistoryIntent.OnHistoryItemClick -> {}
                is HistoryIntent.OnNavigateBack -> onBack()
            }
        }
    )
}