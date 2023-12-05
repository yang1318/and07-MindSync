package boostcamp.and07.mindsync.data.repository.login

import boostcamp.and07.mindsync.data.network.LoginApi
import boostcamp.and07.mindsync.data.network.request.KakaoLoginRequest
import boostcamp.and07.mindsync.ui.util.NetworkExceptionMessage
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(private val api: LoginApi) : LoginRepository {
        override suspend fun loginWithKakao(kakaoUserId: Long): Result<String> {
            return try {
                val request = KakaoLoginRequest(kakaoUserId)
                val response = api.postKakaoOAuth(request)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        Result.success(loginResponse.accessToken)
                    }
                        ?: Result.failure(Throwable(NetworkExceptionMessage.ERROR_MESSAGE_KAKAO_RESULT_NULL.message))
                } else {
                    Result.failure(
                        Throwable(response.code().toString()),
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }