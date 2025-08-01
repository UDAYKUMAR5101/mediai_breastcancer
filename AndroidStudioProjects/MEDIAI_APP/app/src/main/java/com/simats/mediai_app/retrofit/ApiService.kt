package com.simats.mediai_app.retrofit

import com.simats.mediai_app.responses.AuthStatusResponse
import com.simats.mediai_app.responses.ChangepasswordRequest
import com.simats.mediai_app.responses.ChangepasswordResponse
import com.simats.mediai_app.responses.DeleteRequest
import com.simats.mediai_app.responses.DeleteResponse
import com.simats.mediai_app.responses.EmailRequest
import com.simats.mediai_app.responses.EmailResponse
import com.simats.mediai_app.responses.ImageRequest
import com.simats.mediai_app.responses.ImageResponse
import com.simats.mediai_app.responses.LoginRequest
import com.simats.mediai_app.responses.LoginResponse
import com.simats.mediai_app.responses.OtpverifyRequest
import com.simats.mediai_app.responses.OtpverifyResponse
import com.simats.mediai_app.responses.ProfileRequest
import com.simats.mediai_app.responses.ProfileResponse
import com.simats.mediai_app.responses.ResetpasswordRequest
import com.simats.mediai_app.responses.ResetpasswordResponse
import com.simats.mediai_app.responses.SignupRequest
import com.simats.mediai_app.responses.SignupResponse
import com.simats.mediai_app.responses.SymtomsRequest
import com.simats.mediai_app.responses.SymtomsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.GET

interface ApiService {
    @POST("api/register/")
    fun register(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @POST("api/login/")
    fun login(@Body loginRequest : LoginRequest) : Call<LoginResponse>

    @POST("api/symptoms/")
    fun submitSymptoms(@Body symptomsRequest: SymtomsRequest): Call<SymtomsResponse>

    @POST("api/api/upload-medical-image/")
    fun uploadMedicalImage(@Body imageRequest: ImageRequest): Call<ImageResponse>

    @POST("api/api/change-password/")
    fun changePassword(@Body changePasswordRequest: ChangepasswordRequest): Call<ChangepasswordResponse>

    @POST("api/api/delete-account/")
    fun deleteAccount(@Body deleteRequest: DeleteRequest): Call<DeleteResponse>

    @POST("api/request-otp/")
    fun requestOtp(@Body emailrequest: EmailRequest): Call<EmailResponse>

    @POST("api/verify-otp/")
    fun verifyOtp(@Body otp: OtpverifyRequest): Call<OtpverifyResponse>

    @POST("api/reset-password/")
    fun resetPassword(@Body resetPasswordRequest: ResetpasswordRequest): Call<ResetpasswordResponse>

    @POST("api/api/profile/")
    fun createProfile(@Body profileRequest: ProfileRequest): Call<ProfileResponse>

    @PATCH("api/api/profile/")
    fun updateProfile(@Body profileRequest: ProfileRequest): Call<ProfileResponse>

    @GET("api/api/profile/")
    fun getProfile(): Call<ProfileResponse>

    @GET("api/check-auth/")
    fun checkAuthStatus(): Call<AuthStatusResponse>


}
