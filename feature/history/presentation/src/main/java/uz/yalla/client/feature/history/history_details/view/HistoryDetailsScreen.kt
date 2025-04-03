package uz.yalla.client.feature.history.history_details.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history_details.components.OrderDetailsBottomSheet
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsUIState
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
internal fun HistoryDetailsScreen(
    uiState: HistoryDetailsUIState,
    loading: Boolean,
    map: MapStrategy,
    onIntent: (HistoryDetailsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = { HistoryDetailsTopApp { onIntent(HistoryDetailsIntent.NavigateBack) } },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (loading.not()) {
                    map.Map(
                        startingPoint = null,
                        contentPadding = PaddingValues(0.dp),
                        enabled = false,
                        onMapReady = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }

                uiState.orderDetails?.let {
                    OrderDetailsBottomSheet(order = it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailsTopApp(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.order_details),
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelLarge
            )
        }
    )
}