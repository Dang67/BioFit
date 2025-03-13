package com.example.biofit.data.domain

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.biofit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await


class GoogleSignIn(private val context:Context) {
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Được tạo tự động từ google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }


    // lấy intent để login google
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }


    suspend fun signOut() {
        try {
            googleSignInClient.signOut().await()
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e("GoogleSignInManager", "Error signing out", e)
        }
    }

    fun handleSignInResult(data: Intent?, onResult: (String?) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            onResult(idToken)
        } catch (e: ApiException) {
            Log.e("GoogleSignInManager", "Sign-in failed", e)
            onResult(null)
        }
    }

    fun getSignInResultFromIntent(data: Intent?): Task<GoogleSignInAccount> {
        return GoogleSignIn.getSignedInAccountFromIntent(data)
    }


}