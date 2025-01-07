package uz.ildam.technologies.yalla.android.ui.screens.contact_us

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.screens.about_app.AboutAppActionState
import uz.ildam.technologies.yalla.android.ui.screens.about_app.AboutAppUIState
import uz.ildam.technologies.yalla.feature.settings.domain.usecase.GetConfigUseCase

class ContactUsViewModel(
    private val getConfigUseCase: GetConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUsUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<ContactUsActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getConfig() = viewModelScope.launch {
        _actionState.emit(ContactUsActionState.Loading)
        getConfigUseCase()
            .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        socialNetworks = listOf(
                            Triple(
                                R.drawable.ic_telegram,
                                result.setting.telegram,
                                R.string.telegram
                            ),
                            Triple(
                                R.drawable.ic_instagram,
                                result.setting.instagram,
                                R.string.instagram
                            ),
                            Triple(
                                R.drawable.ic_youtube,
                                result.setting.youtube,
                                R.string.youtube
                            ),
                            Triple(
                                R.drawable.ic_facebook,
                                result.setting.facebook,
                                R.string.facebook
                            )
                        )
                    )
                }
                _actionState.emit(ContactUsActionState.Success)
            }
            .onFailure {
                _actionState.emit(ContactUsActionState.Error)
            }
    }
}
