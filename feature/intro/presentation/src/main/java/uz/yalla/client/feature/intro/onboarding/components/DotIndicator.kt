package uz.yalla.client.feature.intro.onboarding.components

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
internal fun DotIndicator(
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
                launch(Dispatchers.Main) {
                    val currentPage = pagerState.currentPage
                    val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
                    val lastVisibleIndex =
                        indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

                    if (lastVisibleIndex != null) {
                        if (currentPage > lastVisibleIndex - 1) {
                            indicatorScrollState
                                .animateScrollToItem(currentPage - size + 2)
                        } else if (currentPage <= firstVisibleItemIndex + 1) {
                            indicatorScrollState
                                .animateScrollToItem((currentPage - 1).coerceAtLeast(0))
                        }
                    }
                }
            }
        )

        LazyRow(
            state = indicatorScrollState,
            modifier = Modifier.width(((6 + 16) * 2 + 3 * (10 + 16)).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                item(key = "item$iteration") {
                    val currentPage = pagerState.currentPage
                    val size by animateDpAsState(
                        label = "dotSize",
                        targetValue = when (iteration) {
                            currentPage -> 16.dp
                            else -> 12.dp
                        },
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(
                                if (pagerState.currentPage == iteration) YallaTheme.color.onBackground
                                else YallaTheme.color.surface,
                                CircleShape
                            )
                            .size(size)
                    )
                }
            }
        }
    }
}
