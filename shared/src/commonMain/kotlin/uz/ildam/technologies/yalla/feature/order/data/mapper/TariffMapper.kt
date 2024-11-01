package uz.ildam.technologies.yalla.feature.order.data.mapper

import uz.ildam.technologies.yalla.core.data.mapper.Mapper
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.data.mapper.orFalse
import uz.ildam.technologies.yalla.feature.order.data.response.tariff.GetTariffsResponse
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

object TariffMapper {
    val mapper: Mapper<GetTariffsResponse?, GetTariffsModel> = { remote ->
        GetTariffsModel(
            map = remote?.map.orEmpty(),
            tariff = remote?.tariff?.map(tariffMapper).orEmpty()
        )
    }

    private val tariffMapper: Mapper<GetTariffsResponse.Tariff?, GetTariffsModel.Tariff> =
        { remote ->
            GetTariffsModel.Tariff(
                category = remote?.category.let(tariffCategoryMapper),
                cityKmCost = remote?.city_km_cost.or0(),
                cost = remote?.cost.or0(),
                description = remote?.description.orEmpty(),
                fixedPrice = remote?.fixed_price.or0(),
                fixedType = remote?.fixed_type.orFalse(),
                icon = remote?.icon.orEmpty(),
                id = remote?.id.or0(),
                inCityLocation = remote?.in_city_location.orFalse(),
                includedKm = remote?.included_km.or0(),
                index = remote?.index.or0(),
                minOutCityCost = remote?.min_out_city_cost.or0(),
                modification = remote?.modification.orEmpty(),
                name = remote?.name.orEmpty(),
                outCityKmCost = remote?.out_city_km_cost.or0(),
                photo = remote?.photo.orEmpty(),
                secondAddress = remote?.second_address.orFalse(),
                services = remote?.services?.map(tariffServiceMapper).orEmpty()
            )
        }

    private val tariffCategoryMapper: Mapper<GetTariffsResponse.Tariff.Category?, GetTariffsModel.Tariff.Category> =
        { remote ->
            GetTariffsModel.Tariff.Category(
                id = remote?.id.or0(),
                name = remote?.name.orEmpty()
            )
        }

    private val tariffServiceMapper: Mapper<GetTariffsResponse.Tariff.Service?, GetTariffsModel.Tariff.Service> =
        { remote ->
            GetTariffsModel.Tariff.Service(
                cost = remote?.cost.or0(),
                costType = remote?.cost_type.orEmpty(),
                id = remote?.id.or0(),
                name = remote?.name.orEmpty()
            )
        }
}