package com.example.biofit.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biofit.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel(){

    fun checkSubscriptionStatus(userId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val isExpired = RetrofitClient.instance.checkSubscription(userId)
                onResult(isExpired)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error checking subscription: ${e.message}")
                onResult(false) // Mặc định là chưa hết hạn nếu lỗi
            }
        }
    }
}