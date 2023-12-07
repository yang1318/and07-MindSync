package boostcamp.and07.mindsync.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewAccessTokenRequest(
    @SerialName("refresh_token")
    val refreshToken: String,
)
