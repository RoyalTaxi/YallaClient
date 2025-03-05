package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.service.order.response.tariff.GetTariffsResponse

object GetTariffMapper {
    val mapper: Mapper<GetTariffsResponse?, GetTariffsModel> = { remote ->
        GetTariffsModel(
            map = remote?.map.let(mapMapper),
            tariff = remote?.tariff?.map(tariffMapper).orEmpty()
        )
    }

    private val mapMapper: Mapper<GetTariffsResponse.Map?, GetTariffsModel.Map> = { remote ->
        GetTariffsModel.Map(
            distance = remote?.distance.or0(),
            duration = remote?.duration.or0(),
            routing = remote?.routing?.map(routeMapper).orEmpty()
        )
    }

    private val routeMapper: Mapper<GetTariffsResponse.Map.Routing?, GetTariffsModel.Map.Routing> =
        { remote ->
            GetTariffsModel.Map.Routing(
                lat = remote?.lat.or0(),
                lng = remote?.lng.or0()
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