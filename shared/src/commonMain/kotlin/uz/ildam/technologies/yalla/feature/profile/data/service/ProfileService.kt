package uz.ildam.technologies.yalla.feature.profile.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
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
import uz.ildam.technologies.yalla.feature.profile.data.response.UpdateAvatarResponse
import uz.ildam.technologies.yalla.feature.profile.data.url.ProfileUrl

class ProfileService(
    private val ktor: HttpClient
) {
    suspend fun getMe(): Result<ApiResponseWrapper<GetMeResponse>, DataError.Network> =
        safeApiCall { ktor.post(ProfileUrl.GET_ME).body() }

    suspend fun updateMe(body: UpdateMeRequest): Result<ApiResponseWrapper<ClientRemoteModel>, DataError.Network> =
        safeApiCall {
            ktor.put(ProfileUrl.UPDATE_ME) { setBody(body) }.body()
        }

    suspend fun updateAvatar(image: ByteArray): Result<ApiResponseWrapper<UpdateAvatarResponse>, DataError.Network> =
        safeApiCall {
            val formData = MultiPartFormDataContent(
                formData {
                    append(
                        key = "photo",
                        value = image,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(
                                HttpHeaders.ContentDisposition,
                                ContentDisposition.File
                                    .withParameter("filename", "my_avatar.jpg")
                                    .toString()
                            )
                        }
                    )
                }
            )
            ktor.post(ProfileUrl.CHANGE_AVATAR) {
                contentType(ContentType.MultiPart.FormData)
                setBody(formData)
            }.body()
        }
}