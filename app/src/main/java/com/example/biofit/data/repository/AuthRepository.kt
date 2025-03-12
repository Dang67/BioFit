package com.example.biofit.data.repository

import android.content.Context
import com.example.biofit.data.model.dto.GoogleAuthDTO
import com.example.biofit.data.model.dto.SocialAccountDTO
import com.example.biofit.data.remote.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await


class AuthRepository (private val context: Context){
    private val apiService = RetrofitClient.authInstance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(idToken: String): Result<SocialAccountDTO> {
        return try {
            val response = apiService.googleSignIn(GoogleAuthDTO(idToken))
            if (response.isSuccessful && response.body() != null) {
                // Lưu token
                saveAuthToken(response.body()!!.accessToken, response.body()!!.refreshToken)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to authenticate: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getGoogleIdToken(googleSignInAccount: GoogleSignInAccount): String? {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            authResult.user?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    private fun saveAuthToken(accessToken: String, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }
}