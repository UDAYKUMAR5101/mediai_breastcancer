package com.simats.mediai_app.retrofit


import com.simats.mediai_app.responses.ChangeRequest
import com.simats.mediai_app.responses.ChangeResponse
import com.simats.mediai_app.responses.ChatRequest
import com.simats.mediai_app.responses.ChatResponse
import com.simats.mediai_app.responses.DeleteAccountRequest
import com.simats.mediai_app.responses.DeleteAccountResponse
import com.simats.mediai_app.responses.LoginRequest
import com.simats.mediai_app.responses.LoginResponse
import com.simats.mediai_app.responses.SignupRequest
import com.simats.mediai_app.responses.SignupResponse
import com.simats.mediai_app.responses.EmailRequest
import com.simats.mediai_app.responses.EmailResponse
import com.simats.mediai_app.responses.ResetRequest
import com.simats.mediai_app.responses.ResetResponse
import com.simats.mediai_app.responses.UploadResponse
import com.simats.mediai_app.responses.VerifyotpRequest
import com.simats.mediai_app.responses.VerifyotpResponse
import com.simats.mediai_app.responses.SymptomsRequest
import com.simats.mediai_app.responses.SymptomsResponse
import com.simats.mediai_app.responses.ImagePredictionResponse
import com.simats.mediai_app.responses.ProfileRequest
import com.simats.mediai_app.responses.ProfileResponse
import com.simats.mediai_app.responses.SaveHistoryRequest
import com.simats.mediai_app.responses.SaveHistoryResponse
import com.simats.mediai_app.responses.GetHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Part

interface ApiService {
    @POST("api/register/")
    fun register(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @POST("api/login/")
    fun login(@Body loginRequest : LoginRequest) : Call<LoginResponse>
    
    @POST("api/request-otp/")
    fun requestOtp(@Body emailRequest: EmailRequest): Call<EmailResponse>

    @POST("api/verify-otp/")
    fun verifyotp(@Body verifyotpRequest : VerifyotpRequest) : Call<VerifyotpResponse>

    @POST("api/reset-password/")
    fun resetPassword(@Body resetPasswordRequest: ResetRequest): Call<ResetResponse>


    @POST("api/api/change-password/")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangeRequest
    ): Call<ChangeResponse>


    @POST("api/api/delete-account/")
    fun deleteAccount(
             @Header("Authorization") token: String,
             @Body deleteAccountRequest: DeleteAccountRequest
            ): Call<DeleteAccountResponse>

    @POST("api/chatbot/")
    fun chatbot(@Header("Authorization") token: String, @Body chatbotRequest: ChatRequest): Call<ChatResponse>

    @POST("api/upload-image/")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>

    @POST("api/predict-symptoms/")
    fun predictSymptoms(@Body symptomsRequest: SymptomsRequest): Call<SymptomsResponse>

    @POST("api/api/gemini-risk/")
    fun predictImageRisk(@Part image: MultipartBody.Part): Call<ImagePredictionResponse>

    // Profile API endpoints
    @POST("api/api/profile/")
    fun createProfile(
        @Header("Authorization") token: String,
        @Part("username") username: okhttp3.RequestBody,
        @Part("age") age: okhttp3.RequestBody,
        @Part("gender") gender: okhttp3.RequestBody,
        @Part("date_of_birth") dateOfBirth: okhttp3.RequestBody,
        @Part("notes") notes: okhttp3.RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ProfileResponse>

    @PATCH("api/api/profile/{id}/")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") profileId: Int,
        @Part("username") username: okhttp3.RequestBody,
        @Part("age") age: okhttp3.RequestBody,
        @Part("gender") gender: okhttp3.RequestBody,
        @Part("date_of_birth") dateOfBirth: okhttp3.RequestBody,
        @Part("notes") notes: okhttp3.RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<ProfileResponse>

    @GET("api/api/profile/")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    // History API endpoints
    @POST("api/history/")
    fun saveHistory(
        @Header("Authorization") token: String,
        @Body saveHistoryRequest: SaveHistoryRequest
    ): Call<SaveHistoryResponse>

    @GET("api/history/")
    fun getHistory(@Header("Authorization") token: String): Call<GetHistoryResponse>
}
