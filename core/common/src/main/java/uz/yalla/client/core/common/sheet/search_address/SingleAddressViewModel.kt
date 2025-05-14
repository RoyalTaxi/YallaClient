package uz.yalla.client.core.common.sheet.search_address

import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.map.domain.usecase.SearchAddressUseCase

class SingleAddressViewModel(
    searchAddressUseCase: SearchAddressUseCase,
    getPolygonUseCase: GetPolygonUseCase,
    getSecondaryAddressedUseCase: GetSecondaryAddressedUseCase
) : BaseAddressSearchViewModel(
    searchAddressUseCase,
    getPolygonUseCase,
    getSecondaryAddressedUseCase
)