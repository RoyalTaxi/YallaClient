package uz.ildam.technologies.yalla.android.ui.screens.about_app


data class AboutAppUIState(
    val privacyPolicyRu: Pair<String, Int>? = null,
    val privacyPolicyUz: Pair<String, Int>? = null,
    val socialNetworks: List<Triple<Int, String, Int>> = emptyList()
)