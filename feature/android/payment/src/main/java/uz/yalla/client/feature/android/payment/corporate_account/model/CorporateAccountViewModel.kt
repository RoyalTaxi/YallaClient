package uz.yalla.client.feature.android.payment.corporate_account.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AddCompanyViewModel(
) : ViewModel(){

    private var _uiState = MutableStateFlow(AddCompanyUiState())
    val uiState = _uiState.asStateFlow()

    private var _actionState = MutableSharedFlow<AddCompanyActionState>()
    val actionState = _actionState.asSharedFlow()


}