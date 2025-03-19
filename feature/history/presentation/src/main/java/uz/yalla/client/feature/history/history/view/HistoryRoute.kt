package uz.yalla.client.feature.history.history.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.feature.history.history.model.HistoryViewModel

@Composable
internal fun HistoryRoute(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {

    val orders = vm.orders.collectAsLazyPagingItems()
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { vm.getOrders() }

    loading = when (orders.loadState.refresh) {
        LoadState.Loading -> true
        else -> false
    }

    HistoryScreen(
        orders = orders,
        onIntent = { intent ->
            when (intent) {
                is HistoryIntent.OnHistoryItemClick -> onClickItem(intent.id.toInt())
                is HistoryIntent.OnNavigateBack -> onBack()
            }
        }
    )

    if (loading) LoadingDialog()
}