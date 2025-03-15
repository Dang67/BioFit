package com.example.biofit.data.remote

import android.util.Log
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.model.dto.ExerciseDTO
import com.example.biofit.data.model.dto.ExerciseDetailDTO
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.model.request.LoginRequest
import com.example.biofit.data.model.request.RegisterRequest
import com.example.biofit.data.model.request.UpdateUserRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // User API
    @POST("api/user/login")
    fun login(@Body request: LoginRequest): Call<UserDTO>

    @POST("api/user/register")
    fun register(@Body request: RegisterRequest): Call<UserDTO>

    /*@PUT("api/user/update/{userId}")
    fun updateUser(
        @Path("userId") userId: Long,
        @Body request: UpdateUserRequest
    ): Call<UserDTO>*/

    @PUT("api/user/update/{userId}")
    @Multipart
    fun updateUser(
        @Path("userId") userId: Long,
        @Part avatar: MultipartBody.Part?,
        @Part("user") user: RequestBody
    ): Call<UserDTO>


    /*
    ------------------------------------------------------------------------------------------------
*/
    // Daily Log API
    @GET("api/daily-log/user/{userId}/latest")
    fun getLatestDailyLog(
        @Path("userId") userId: Long
    ): Call<DailyLogDTO>

    @POST("/api/daily-log/save-or-update")
    fun saveOrUpdateDailyWeight(@Body request: DailyLogDTO): Call<DailyLogDTO>

    @GET("/api/daily-log/user/{userId}/history")
    fun getWeightHistory(@Path("userId") userId: Long): Call<List<DailyLogDTO>>

/*
----------------------------------------------------------------------------------------------------
*/
    // Exercise API
    @GET("api/exercise/user/{userId}")
    fun getExercises(@Path("userId") userId: Long): Call<List<ExerciseDTO>>

    @GET("api/exercise/{exerciseId}/details")
    fun getExerciseByGoalAndIntensity(
        @Path("exerciseId") exerciseId: Long,
        @Query("exerciseGoal") exerciseGoal: Int,
        @Query("intensity") intensity: Int
    ): Call<ExerciseDTO>

    @POST("api/exercise/create")
    fun createExercise(@Body exerciseDTO: ExerciseDTO): Call<ExerciseDTO>

    @DELETE("api/exercise/{exerciseId}")
    fun deleteExercise(@Path("exerciseId") exerciseId: Long): Call<Void>

    @PUT("api/exercise/{exerciseId}")
    fun updateExercise(@Path("exerciseId") exerciseId: Long, @Body exercise: ExerciseDTO): Call<Void>
}
