package uz.yalla.client.core.common.sheet.search_address

import uz.yalla.client.feature.home.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.home.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.home.domain.usecase.SearchAddressUseCase

class SingleAddressViewModel(
    searchAddressUseCase: SearchAddressUseCase,
    getPolygonUseCase: GetPolygonUseCase,
    getSecondaryAddressedUseCase: GetSecondaryAddressedUseCase
) : BaseAddressSearchViewModel(
    searchAddressUseCase,
    getPolygonUseCase,
    getSecondaryAddressedUseCase
)