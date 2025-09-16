package uz.yalla.client.feature.history.history_details.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history_details.components.OrderDetailsBottomSheet
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsIntent
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsState

@Composable
fun HistoryDetailsScreen(
    state: HistoryDetailsState,
    onIntent: (HistoryDetailsIntent) -> Unit,
    map: @Composable (modifier: Modifier) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = { HistoryDetailsTopBar { onIntent(HistoryDetailsIntent.NavigateBack) } },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                map(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                state.orderDetails?.let {
                    OrderDetailsBottomSheet(order = it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailsTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.order_details),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.labelLarge
            )
        }
    )
}