package uz.yalla.client.feature.core.utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState

sealed interface ScrollBehavior {
    suspend fun scrollBy(listState: LazyListState, distance: Float)
    suspend fun scrollToItem(listState: LazyListState, index: Int)
}

data object InstantScroll : ScrollBehavior {
    override suspend fun scrollBy(listState: LazyListState, distance: Float) {
        listState.scrollBy(distance)
    }

    override suspend fun scrollToItem(listState: LazyListState, index: Int) {
        listState.scrollToItem(index)
    }
}

data object AnimatedScroll : ScrollBehavior {
    override suspend fun scrollBy(listState: LazyListState, distance: Float) {
        listState.animateScrollBy(distance, animationSpec = tween(200))
    }

    override suspend fun scrollToItem(listState: LazyListState, index: Int) {
        listState.animateScrollToItem(index)
    }
}