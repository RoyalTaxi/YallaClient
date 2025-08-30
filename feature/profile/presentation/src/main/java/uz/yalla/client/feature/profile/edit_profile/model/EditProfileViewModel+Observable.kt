package uz.yalla.client.feature.profile.edit_profile.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import uz.yalla.client.feature.profile.edit_profile.intent.EditProfileSideEffect

fun EditProfileViewModel.observerDatePickerVisibility() = viewModelScope.launch {
    container.stateFlow
        .distinctUntilChangedBy { it.isDatePickerVisible }
        .collectLatest { state ->
            intent {
                postSideEffect(
                    EditProfileSideEffect.SetDatePickerVisibility(
                        visibility = state.isDatePickerVisible
                    )
                )
            }
        }
}