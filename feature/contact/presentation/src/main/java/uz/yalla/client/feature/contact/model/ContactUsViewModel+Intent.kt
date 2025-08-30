package uz.yalla.client.feature.contact.model

import uz.yalla.client.feature.contact.intent.ContactUsIntent
import uz.yalla.client.feature.contact.intent.ContactUsSideEffect

fun ContactUsViewModel.onIntent(intent: ContactUsIntent) = intent {
    when (intent) {
        ContactUsIntent.OnNavigateBack -> postSideEffect(ContactUsSideEffect.NavigateBack)
        is ContactUsIntent.OnClickUrl -> postSideEffect(
            ContactUsSideEffect.NavigateWeb(
                title = intent.title,
                url = intent.url
            )
        )

        is ContactUsIntent.OnClickEmail -> postSideEffect(
            ContactUsSideEffect.NavigateToEmail(
                email = intent.email
            )
        )

        is ContactUsIntent.OnClickPhone -> postSideEffect(
            ContactUsSideEffect.NavigateToPhoneCall(
                phoneNumber = intent.phone
            )
        )
    }
}