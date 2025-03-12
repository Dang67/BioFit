package com.example.biofit.data.remote

import com.example.biofit.data.model.dto.GoogleAuthDTO
import com.example.biofit.data.model.dto.SocialAccountDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // Google API
    @POST("api/auth/google")
    suspend fun googleSignIn(@Body request: GoogleAuthDTO): Response<SocialAccountDTO>
}