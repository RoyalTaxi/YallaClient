package uz.yalla.client.feature.order.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.order.data.di.OrderData
import uz.yalla.client.feature.order.presentation.cancel_reason.model.CancelReasonViewModel
import uz.yalla.client.feature.order.presentation.client_waiting.model.ClientWaitingViewModel
import uz.yalla.client.feature.order.presentation.driver_waiting.model.DriverWaitingViewModel
import uz.yalla.client.feature.order.presentation.feedback.model.FeedbackSheetViewModel
import uz.yalla.client.feature.order.presentation.main.model.MainSheetViewModel
import uz.yalla.client.feature.order.presentation.no_service.model.NoServiceViewModel
import uz.yalla.client.feature.order.presentation.on_the_ride.model.OnTheRideSheetViewModel
import uz.yalla.client.feature.order.presentation.order_canceled.model.OrderCanceledSheetViewModel
import uz.yalla.client.feature.order.presentation.search.model.SearchCarSheetViewModel

object Order {
    private val viewModelModule = module {
        viewModelOf(::MainSheetViewModel)
        viewModelOf(::SearchCarSheetViewModel)
        viewModelOf(::CancelReasonViewModel)
        viewModelOf(::OrderCanceledSheetViewModel)
        viewModelOf(::ClientWaitingViewModel)
        viewModelOf(::DriverWaitingViewModel)
        viewModelOf(::OnTheRideSheetViewModel)
        viewModelOf(::FeedbackSheetViewModel)
        viewModelOf(::NoServiceViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *OrderData.modules.toTypedArray()
    )
}