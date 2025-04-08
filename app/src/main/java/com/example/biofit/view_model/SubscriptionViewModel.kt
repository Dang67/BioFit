package com.example.biofit.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biofit.data.model.response.SubscriptionResponse
import com.example.biofit.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionViewModel : ViewModel() {
    private val _subscription = MutableStateFlow<SubscriptionResponse?>(null)
    val subscription: StateFlow<SubscriptionResponse?> = _subscription.asStateFlow()


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

    fun fetchSubscription(userId: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getLatestSubscription(userId).execute().body()
                }
                _subscription.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching subscription: ${e.message}")
                _subscription.value = null // Không có subscription hoặc lỗi
            }
        }
    }
}