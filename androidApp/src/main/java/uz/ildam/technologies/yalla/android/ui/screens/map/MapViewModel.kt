package uz.ildam.technologies.yalla.android.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.order.domain.usecase.map.GetPolygonUseCase

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    fun getPolygon() = viewModelScope.launch {
        when (val result = getPolygonUseCase()) {
            is Result.Error -> {
                Log.d("AAA", "getPolygon: ${result.error}")
            }

            is Result.Success -> {}
        }
    }
}