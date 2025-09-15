package uz.yalla.client.core.common.map.lite

import androidx.compose.runtime.Composable
import uz.yalla.client.core.domain.model.MapPoint

interface LiteMap {

    @Composable
    fun View(initialLocation: MapPoint)
}

