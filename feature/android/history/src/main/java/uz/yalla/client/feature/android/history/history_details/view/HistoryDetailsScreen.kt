package uz.yalla.client.feature.android.history.history_details.view

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.history.R
import uz.yalla.client.feature.android.history.history_details.components.OrderDetailsBottomSheet
import uz.yalla.client.feature.android.history.history_details.model.HistoryDetailsUIState
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.map.MapStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryDetailsScreen(
    uiState: HistoryDetailsUIState,
    loading: Boolean,
    map: MapStrategy,
    onIntent: (HistoryDetailsIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.white),
                navigationIcon = {
                    IconButton(onClick = { onIntent(HistoryDetailsIntent.NavigateBack) }) {
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
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (loading.not()) map.Map(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .pointerInput(Unit) { detectTapGestures { } },
                    contentPadding = PaddingValues(0.dp)
                )

                uiState.orderDetails?.let {
                    OrderDetailsBottomSheet(order = it)
                }
            }
        }
    )
}