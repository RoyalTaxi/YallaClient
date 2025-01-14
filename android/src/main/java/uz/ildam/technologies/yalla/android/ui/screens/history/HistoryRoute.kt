package uz.ildam.technologies.yalla.android.ui.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
    vm: HistoryViewModel = koinViewModel()
) {
    val orders = vm.orders.collectAsLazyPagingItems()
    var loading by remember { mutableStateOf(false) }

    loading = when (orders.loadState.refresh) {
        is LoadState.Loading -> true
        else -> false
    }

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

    if (loading) LoadingDialog()
}