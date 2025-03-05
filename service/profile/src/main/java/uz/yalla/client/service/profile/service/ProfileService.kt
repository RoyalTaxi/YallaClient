package uz.yalla.client.service.profile.service

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
import uz.yalla.client.service.profile.request.UpdateMeRequest
import uz.yalla.client.service.profile.response.GetMeResponse
import uz.yalla.client.service.profile.response.UpdateAvatarResponse
import uz.yalla.client.service.profile.url.ProfileUrl
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.core.service.model.ApiResponseWrapper
import uz.yalla.client.core.service.model.ClientRemoteModel
import uz.yalla.client.core.service.network.safeApiCall

class ProfileService(
    private val ktor: HttpClient
) {
    suspend fun getMe(): Either<ApiResponseWrapper<GetMeResponse>, DataError.Network> =
        safeApiCall { ktor.post(ProfileUrl.GET_ME).body() }

    suspend fun updateMe(body: UpdateMeRequest): Either<ApiResponseWrapper<ClientRemoteModel>, DataError.Network> =
        safeApiCall {
            ktor.put(ProfileUrl.UPDATE_ME) { setBody(body) }.body()
        }

    suspend fun updateAvatar(image: ByteArray): Either<ApiResponseWrapper<UpdateAvatarResponse>, DataError.Network> =
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