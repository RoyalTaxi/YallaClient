package uz.ildam.technologies.yalla.feature.auth.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ValidateAuthCodeResponse(
    val is_client: Boolean?,
    val token_type: String?,
    val access_token: String?,
    val expires_in: Int?,
    val client: Client
) {
    @Serializable
    data class Client(
        val id: Int?,
        val phone: String?,
        val given_names: String?,
        val father_name: String?,
        val sur_name: String?,
        val block: Boolean?,
        val balance: Int?,
        val block_note: String?,
        val rating: Int?,
        val block_date: String?,
        val block_source: String?,
        val image: String?,
        val block_expiry: String?,
        val brand: Brand?,
        val created_at: String?,
        val creator_type: String?,
        val birthday: String?,
        val gender: String
    ) {
        @Serializable
        data class Brand(
            val id: Int?,
            val name: String
        )
    }
}