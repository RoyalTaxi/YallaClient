package uz.yalla.client.core.common.sheet.search_address

import kotlinx.coroutines.flow.MutableStateFlow
import uz.yalla.client.feature.home.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.home.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.home.domain.usecase.SearchAddressUseCase

class DualAddressViewModel(
    searchAddressUseCase: SearchAddressUseCase,
    getPolygonUseCase: GetPolygonUseCase,
    getSecondaryAddressedUseCase: GetSecondaryAddressedUseCase
) : BaseAddressSearchViewModel(
    searchAddressUseCase,
    getPolygonUseCase,
    getSecondaryAddressedUseCase
), DestinationSearchContext {

    override val destinationQuery = MutableStateFlow("")

    override val baseViewModel: BaseAddressSearchViewModel
        get() = this

    init {
        setupDestinationQueryFlow()
    }

    override fun resetSearchState() {
        super.resetSearchState()
        resetDestinationQuery()
    }
}