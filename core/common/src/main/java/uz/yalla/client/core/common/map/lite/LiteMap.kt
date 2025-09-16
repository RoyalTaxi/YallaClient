package uz.yalla.client.core.common.map.lite

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.client.core.common.map.lite.model.LiteMapViewModel
import uz.yalla.client.core.domain.model.MapPoint

interface LiteMap {

    @Composable
    fun View(
        modifier: Modifier,
        viewModel: LiteMapViewModel
    )
}

