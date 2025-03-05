package uz.yalla.client.feature.order.presentation.search.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SearchCarSheetState(
    val loading: Boolean = false,

    val sheetHeight: Dp = 0.dp,
    val footerHeight: Dp = 0.dp,
    val detailsBottomSheetVisibility: Boolean = false,
)