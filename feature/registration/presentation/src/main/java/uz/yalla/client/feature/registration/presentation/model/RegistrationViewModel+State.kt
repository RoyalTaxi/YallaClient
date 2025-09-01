package uz.yalla.client.feature.registration.presentation.model

import org.threeten.bp.LocalDate

internal fun RegistrationViewModel.setDatePickerVisible(visible: Boolean) = intent {
    intent {
        reduce {
            state.copy(isDatePickerVisible = visible)
        }
    }
}

internal fun RegistrationViewModel.setDateOfBirth(date: LocalDate) = intent {
    intent {
        reduce {
            state.copy(dateOfBirth = date)
        }
    }
}

internal fun RegistrationViewModel.setFirstName(firstName: String) = intent {
    intent {
        reduce {
            state.copy(firstName = firstName)
        }
    }
}

internal fun RegistrationViewModel.setGender(gender: Gender) = intent {
    intent {
        reduce {
            state.copy(gender = gender)
        }
    }
}

internal fun RegistrationViewModel.setLastName(lastName: String) = intent {
    intent {
        reduce {
            state.copy(lastName = lastName)
        }
    }
}