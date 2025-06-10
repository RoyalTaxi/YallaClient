package uz.yalla.client.feature.info.about_app.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.info.R
import uz.yalla.client.feature.setting.domain.usecase.GetConfigUseCase

internal class AboutAppViewModel(
    private val getConfigUseCase: GetConfigUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AboutAppUIState())
    val uiState = _uiState.asStateFlow()

    fun getConfig() = viewModelScope.launchWithLoading {
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
                prefs.setSupportNumber(result.setting.supportPhone)
            }
            .onFailure(::handleException)
    }
}