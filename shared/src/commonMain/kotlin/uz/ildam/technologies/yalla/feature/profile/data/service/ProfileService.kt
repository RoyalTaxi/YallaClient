package uz.ildam.technologies.yalla.feature.profile.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import uz.ildam.technologies.yalla.core.data.exception.safeApiCall
import uz.ildam.technologies.yalla.core.data.response.ApiResponseWrapper
import uz.ildam.technologies.yalla.core.data.response.ClientRemoteModel
import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.profile.data.request.UpdateMeRequest
import uz.ildam.technologies.yalla.feature.profile.data.response.GetMeResponse
import uz.ildam.technologies.yalla.feature.profile.data.url.ProfileUrl

class ProfileService(
    private val ktor: HttpClient
) {
    suspend fun getMe(): Result<ApiResponseWrapper<GetMeResponse>, DataError.Network> =
        safeApiCall { ktor.post(ProfileUrl.GET_ME).body() }

    suspend fun updateMe(body: UpdateMeRequest): Result<ApiResponseWrapper<ClientRemoteModel>, DataError.Network> =
        safeApiCall {
            val formData = MultiPartFormDataContent(
                formData {
                    append(key = "gender", value = body.gender)
                    append(key = "given_names", value = body.given_names)
                    append(key = "sur_name", value = body.sur_name)
                    append(key = "birthday", value = body.birthday)
                    append(
                        key = "image",
                        value = body.image,
                        headers = Headers.build { append(HttpHeaders.ContentType, "image/jpeg") }
                    )
                }
            )

            ktor.post(ProfileUrl.UPDATE_ME) {
                contentType(ContentType.MultiPart.FormData)
                setBody(formData)
            }.body()
        }
}