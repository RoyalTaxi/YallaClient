package uz.yalla.client.feature.android.info.about_app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.settings.domain.usecase.GetConfigUseCase
import uz.yalla.client.feature.android.info.R

internal class AboutAppViewModel(
    private val getConfigUseCase: GetConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutAppUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<AboutAppActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getConfig() = viewModelScope.launch {
        _actionState.emit(AboutAppActionState.Loading)
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
                        ),
                        privacyPolicyRu = result.setting.privacyPolicyRu to R.string.user_agreement,
                        privacyPolicyUz = result.setting.privacyPolicyUz to R.string.user_agreement
                    )
                }
                AppPreferences.supportNumber = result.setting.supportPhone
                _actionState.emit(AboutAppActionState.Success)
            }
            .onFailure {
                _actionState.emit(AboutAppActionState.Error)
            }
    }
}
