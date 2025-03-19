package uz.yalla.client.feature.payment.corporate_account.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
internal fun PagerIndicator(
    pageCount: Int,
    pagerState: PagerState,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val indicatorScrollState = rememberLazyListState()

        LaunchedEffect(
            key1 = pagerState.currentPage,
            block = {
                val currentPage = pagerState.currentPage
                val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
                val lastVisibleIndex = indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

                if (lastVisibleIndex != null) {
                    if (currentPage > lastVisibleIndex - 1) {
                        indicatorScrollState.animateScrollToItem(currentPage - size + 2)
                    } else if (currentPage <= firstVisibleItemIndex + 1) {
                        indicatorScrollState.animateScrollToItem((currentPage - 1).coerceAtLeast(0))
                    }
                }
            }
        )

        LazyRow(
            state = indicatorScrollState,
            modifier = Modifier.width((40 * 3 + 20).dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                item(key = "item$iteration") {
                    val indicatorWidth by animateDpAsState(
                        label = "dotWidth",
                        targetValue = 40.dp
                    )

                    val indicatorHeight by animateDpAsState(
                        label = "dotHeight",
                        targetValue = 6.dp
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(
                                if (pagerState.currentPage == iteration) YallaTheme.color.primary
                                else YallaTheme.color.gray2,
                                CircleShape
                            )
                            .size(indicatorWidth, indicatorHeight)
                    )
                }
            }
        }
    }
}