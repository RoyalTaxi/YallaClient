package uz.ildam.technologies.yalla.android.ui.components.indicator


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
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun DotIndicator(
    pageCount: Int,
    pagerState: PagerState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val indicatorScrollState = rememberLazyListState()

        LaunchedEffect(
            key1 = pagerState.currentPage,
            block = {
                val currentPage = pagerState.currentPage
                val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
                val lastVisibleIndex =
                    indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
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

        val primary = YallaTheme.color.black
        val secondary = YallaTheme.color.gray2

        LazyRow(
            state = indicatorScrollState,
            modifier = Modifier.width(((6 + 16) * 2 + 3 * (10 + 16)).dp), // I'm hard computing it to simplify
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) primary else secondary
                item(key = "item$iteration") {
                    val currentPage = pagerState.currentPage
                    val size by animateDpAsState(
                        targetValue = when (iteration) {
                            currentPage -> 16.dp
                            else -> 12.dp
                        }
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(color, CircleShape)
                            .size(size)
                    )
                }
            }
        }
    }
}
