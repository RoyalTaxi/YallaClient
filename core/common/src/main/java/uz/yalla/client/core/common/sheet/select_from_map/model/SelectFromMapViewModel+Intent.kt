package uz.yalla.client.core.common.sheet.select_from_map.model

import uz.yalla.client.core.common.map.lite.intent.LiteMapIntent
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapEffect.NavigateBack
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapEffect.SelectLocation
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapIntent

fun SelectFromMapViewModel.onIntent(intent: SelectFromMapIntent) = intent {
    when (intent) {
        SelectFromMapIntent.AnimateToMyLocation -> liteMapViewModel.onIntent(LiteMapIntent.AnimateToMyLocation)
        SelectFromMapIntent.NavigateBack -> postSideEffect(NavigateBack)
        SelectFromMapIntent.SelectLocation -> state.location?.let { postSideEffect(SelectLocation(it)) }
        is SelectFromMapIntent.SetViewValue -> reduce { state.copy(viewValue = intent.value) }
        is SelectFromMapIntent.SetSheetHeight -> reduce { state.copy(sheetHeight = intent.height) }
    }
}