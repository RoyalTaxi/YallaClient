package uz.yalla.client.feature.history.history_details.model

internal sealed interface HistoryDetailsActionState {
    data object DetailsSuccess : HistoryDetailsActionState
    data object RouteSuccess : HistoryDetailsActionState
    data object Error : HistoryDetailsActionState
    data object Loading : HistoryDetailsActionState
}