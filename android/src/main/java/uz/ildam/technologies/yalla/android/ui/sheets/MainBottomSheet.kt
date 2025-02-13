package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import uz.yalla.client.feature.core.sheets.SheetValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainBottomSheet(
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    orderTaxi: @Composable () -> Unit,
    tariffInfo: @Composable () -> Unit
) {
    val fraction by remember {
        derivedStateOf {
            val partialOffset =
                scaffoldState.sheetState.values.positionOf(SheetValue.PartiallyExpanded)
            val expandedOffset = scaffoldState.sheetState.values.positionOf(SheetValue.Expanded)
            val current = scaffoldState.sheetState.offset
            if (partialOffset == expandedOffset) 0f
            else {
                val rawFraction = (partialOffset - current) / (partialOffset - expandedOffset)
                rawFraction.coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight(.9f)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { alpha = 1f - fraction }
                .zIndex(if (fraction < 0.5f) 1f else 0f)
        ) {
            orderTaxi()
        }

        Box(
            modifier = Modifier
                .graphicsLayer { alpha = fraction }
                .zIndex(if (fraction >= 0.5f) 1f else 0f)
                .matchParentSize()
                .pointerInput(fraction) {
                    if (fraction > 0f) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    }
                }
        ) {
            tariffInfo()
        }
    }
}