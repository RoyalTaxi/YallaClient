package uz.yalla.client.core.common.map.static

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel

interface StaticMap {

    @Composable
    fun View(
        modifier: Modifier,
        viewModel: StaticMapViewModel,
    )
}

