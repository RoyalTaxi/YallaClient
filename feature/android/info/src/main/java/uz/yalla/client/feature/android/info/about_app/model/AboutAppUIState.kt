package uz.yalla.client.feature.android.info.about_app.model


internal data class AboutAppUIState(
    val privacyPolicyRu: Pair<String, Int>? = null,
    val privacyPolicyUz: Pair<String, Int>? = null,
    val socialNetworks: List<Triple<Int, String, Int>> = emptyList()
)