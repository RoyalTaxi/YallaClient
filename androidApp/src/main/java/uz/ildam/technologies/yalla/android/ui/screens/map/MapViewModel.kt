package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.data.response.map.PolygonResponseItem
import uz.ildam.technologies.yalla.feature.order.domain.usecase.map.GetPolygonUseCase

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    private var polygon = mutableListOf<PolygonResponseItem>()

    fun getPolygon() = viewModelScope.launch {
        when (val result = getPolygonUseCase()) {
            is Result.Error -> {}

            is Result.Success -> {
//                polygon
//                polygon = result.data
            }
        }
    }
}