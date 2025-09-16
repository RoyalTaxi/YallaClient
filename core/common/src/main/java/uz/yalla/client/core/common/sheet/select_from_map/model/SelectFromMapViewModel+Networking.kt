package uz.yalla.client.core.common.sheet.select_from_map.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint

fun SelectFromMapViewModel.getAddressName(point: MapPoint) = viewModelScope.launch {
    getAddressNameUseCase(point.lat, point.lng).onSuccess { data ->
        intent {
            reduce {
                state.copy(
                    location = Location(
                        name = data.displayName,
                        addressId = data.id,
                        point = MapPoint(
                            lat = data.lat,
                            lng = data.lng
                        )
                    )
                )
            }
        }
    }
}

fun SelectFromMapViewModel.getIsWorking(point: MapPoint) = viewModelScope.launch {
    getTariffsUseCase(
        optionIds = emptyList(),
        coords = listOf(point.lat to point.lng)
    ).onSuccess { data ->
        intent {
            reduce {
                state.copy(
                    isWorking = data.working.isWorking
                )
            }
        }
    }
}