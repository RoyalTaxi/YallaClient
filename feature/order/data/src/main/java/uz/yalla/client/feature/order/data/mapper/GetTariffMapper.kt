package uz.yalla.client.feature.order.data.mapper

import uz.yalla.client.core.data.mapper.Mapper
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.core.service.model.ServiceRemoteModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.AwardPaymentType
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.service.order.response.tariff.GetTariffsResponse

object GetTariffMapper {
    val mapper: Mapper<GetTariffsResponse?, GetTariffsModel> = { remote ->
        GetTariffsModel(
            map = remote?.map.let(mapMapper),
            tariff = remote?.tariff?.map(tariffMapper).orEmpty(),
            working = remote?.working.let(workingRemoteModelMapper)
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
                award = remote?.award.let(awardMapper),
                category = remote?.category.let(tariffCategoryMapper),
                cost = remote?.cost.or0(),
                description = remote?.description.orEmpty(),
                fixedPrice = remote?.fixed_price.or0(),
                fixedType = remote?.fixed_type.orFalse(),
                icon = remote?.icon.orEmpty(),
                id = remote?.id.or0(),
                index = remote?.index.or0(),
                name = remote?.name.orEmpty(),
                photo = remote?.photo.orEmpty(),
                isSecondAddressMandatory = remote?.second_address.orFalse(),
                services = remote?.services?.map(tariffServiceRemoteModelMapper).orEmpty(),

                )
        }

    private val tariffCategoryMapper: Mapper<GetTariffsResponse.Tariff.Category?, GetTariffsModel.Tariff.Category> =
        { remote ->
            GetTariffsModel.Tariff.Category(
                id = remote?.id.or0(),
                name = remote?.name.orEmpty()
            )
        }

    private val awardMapper: Mapper<GetTariffsResponse.Tariff.Award?, GetTariffsModel.Tariff.Award?> =
        { remote ->
            remote?.let {
                GetTariffsModel.Tariff.Award(
                    type = AwardPaymentType.fromTypeName(it.cash_or_percentage.orEmpty()),
                    value = it.value.or0()
                )
            }
        }

    private val tariffServiceRemoteModelMapper: Mapper<ServiceRemoteModel?, ServiceModel> =
        { remote ->
            ServiceModel(
                cost = remote?.cost.or0(),
                costType = remote?.costType ?: ServiceModel.COST_TYPE_COST,
                id = remote?.id.or0(),
                name = remote?.name.orEmpty()
            )
        }

    private val workingRemoteModelMapper: Mapper<GetTariffsResponse.Working?, GetTariffsModel.Working> = { remote ->
        GetTariffsModel.Working(
            addressId = remote?.address_id.or0(),
            isWorking = remote?.is_working.orFalse()
        )
    }
}