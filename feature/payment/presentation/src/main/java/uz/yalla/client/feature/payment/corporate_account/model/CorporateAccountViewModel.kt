package uz.yalla.client.feature.payment.corporate_account.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.payment.corporate_account.view.CorporateAccountIntent
import kotlinx.coroutines.Dispatchers

internal class CorporateAccountViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(CorporateAccountUIState())
    val uiState = _uiState.asStateFlow()

    private var _actionState = MutableSharedFlow<CorporateAccountActionState>()
    val actionState = _actionState.asSharedFlow()

    fun processIntent(intent: CorporateAccountIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentState ->
                when (intent) {
                    is CorporateAccountIntent.SetCompanyName -> currentState.copy(name = intent.name)
                    is CorporateAccountIntent.SetCity -> currentState.copy(city = intent.city)
                    is CorporateAccountIntent.SetPerson -> currentState.copy(contactPerson = intent.person)
                    is CorporateAccountIntent.SetNumber -> currentState.copy(number = intent.number)
                    is CorporateAccountIntent.SetEmail -> currentState.copy(email = intent.email)
                    is CorporateAccountIntent.SetBankName -> currentState.copy(bankName = intent.bankName)
                    is CorporateAccountIntent.SetCurrentAccount -> currentState.copy(currentAccount = intent.currentAccount)
                    is CorporateAccountIntent.SetMFO -> currentState.copy(mfo = intent.mfo)
                    is CorporateAccountIntent.SetIndex -> currentState.copy(index = intent.index)
                    is CorporateAccountIntent.SetStreet -> currentState.copy(street = intent.street)
                    is CorporateAccountIntent.SetHomeOffice -> currentState.copy(homeOffice = intent.homeOffice)
                    CorporateAccountIntent.sendData -> TODO()
                }
            }
        }
    }
}