package uz.yalla.client.feature.android.history.history_details.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.yalla.client.feature.android.history.R
import uz.yalla.client.feature.android.history.history_details.components.OrderDetailsBottomSheet
import uz.yalla.client.feature.android.history.history_details.model.HistoryDetailsUIState
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.map.MapStrategy
import uz.yalla.client.feature.core.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun HistoryDetailsScreen(
    uiState: HistoryDetailsUIState,
    loading: Boolean,
    map: MapStrategy,
    onIntent: (HistoryDetailsIntent) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        defineValues = {
            SheetValue.Collapsed at offset(50)
            SheetValue.PartiallyExpanded at offset(50)
            SheetValue.Expanded at contentHeight
        }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState = sheetState)
    val bottomPadding by remember {
        derivedStateOf { scaffoldState.sheetState.requireSheetVisibleHeightDp() }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RectangleShape,
        sheetContainerColor = YallaTheme.color.white,
        sheetDragHandle = null,
        modifier = Modifier.fillMaxSize(),
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
            Box(modifier = Modifier.padding(paddingValues)) {
                if (loading.not()) map.Map(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomPadding)
                )

                Box(
                    modifier = Modifier
                        .pointerInput(Unit) { detectTapGestures { } }
                        .matchParentSize()
                )
            }

        },
        sheetContent = {
            uiState.orderDetails?.let { OrderDetailsBottomSheet(order = it) }
        }
    )
}