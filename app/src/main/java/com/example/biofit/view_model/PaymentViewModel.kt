package com.example.biofit.view_model

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biofit.data.model.request.PaymentRequest
import com.example.biofit.data.model.response.PaymentResponse
import com.example.biofit.data.model.response.SubscriptionResponse
import com.example.biofit.data.remote.RetrofitClient
import com.example.biofit.data.utils.UserSharedPrefsHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.NetworkInterface
import java.util.Locale

class PaymentViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance

    private val _paymentUrl = MutableLiveData<String>()
    val paymentUrl: LiveData<String> = _paymentUrl

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _subscriptionActive = MutableLiveData<Boolean>()
    val subscriptionActive: LiveData<Boolean> = _subscriptionActive

    fun createMoMoPayment(userId: Long, planType: String, amount: Long, context: Context) {
        _loading.value = true

        val packageCode = when (planType) {
            "YEARLY" -> "YEARLY"
            "MONTHLY" -> "MONTHLY"
            else -> planType
        }

        val backendDomain = extractBaseUrl(RetrofitClient.BASE_URL)
        val ipAddress = getIPAddress(true)

        viewModelScope.launch {
            val request = PaymentRequest(
                userId = userId,
                planType = packageCode,
                paymentMethod = "MOMO",
                returnUrl = "$backendDomain/api/payment/momo-return",
                ipAddress = ipAddress,
                amount = amount
            )

            apiService.createPayment(request).enqueue(object : Callback<PaymentResponse> {
                override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                    _loading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val paymentResponse = response.body()!!
                        if (paymentResponse.success) {
                            saveOrderId(context, paymentResponse.orderId)
                            _paymentUrl.value = paymentResponse.paymentUrl ?: ""

                            try {
                                // Tạo intent để mở ứng dụng MoMo với URL thanh toán
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentResponse.paymentUrl))
                                context.startActivity(intent)

                                // Thông báo nếu mở thành công
                                _success.value = "Đang mở ứng dụng MoMo để thanh toán..."
                                Log.d("PaymentViewModel", "Mở ứng dụng MoMo thành công")

                            } catch (e: Exception) {
                                // Thử phương án dự phòng - mở bằng app
                                _success.value = "Đang chuyển hướng đến trang thanh toán trên web..."
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentResponse.paymentUrl))
                                    context.startActivity(browserIntent)
                                } catch (e2: Exception) {
                                    // Nếu cả hai phương án đều thất bại
                                    _error.value = "Không thể mở trang thanh toán: ${e2.message}"
                                    Log.e("PaymentViewModel", "Lỗi khi mở trình thanh toán", e)
                                }
                            }
                        } else {
                            _error.value = paymentResponse.message
                        }
                    } else {
                        _error.value = "Error: ${response.errorBody()?.string()}"
                    }
                }

                override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                    _loading.value = false
                    _error.value = "Network error: ${t.message}"
                }
            })
        }
    }

    fun createVnPayPayment(userId: Long, planType: String, amount: Long, context: Context) {
        _loading.value = true

        // Chuyển đổi planType thành mã gói cụ thể
        val packageCode = when (planType) {
            "YEARLY" -> "YEARLY"
            "MONTHLY" -> "MONTHLY"
            else -> planType
        }

        // Lấy địa chỉ IP của thiết bị (hoặc mạng)
        val ipAddress = getIPAddress(true)

        viewModelScope.launch {
            try {
                // Sử dụng URL backend thực tế để quay lại
                val backendDomain = extractBaseUrl(RetrofitClient.BASE_URL)
                val request = PaymentRequest(
                    userId = userId,
                    planType = packageCode,
                    paymentMethod = "VNPAY",
                    amount = amount,
                    ipAddress = ipAddress,
                    // Sử dụng URL trả về của backend thay vì deep link trực tiếp
                    returnUrl = "${backendDomain}/api/payment/vnPay-return"
                )


                apiService.createPayment(request).enqueue(object : Callback<PaymentResponse> {
                    override fun onResponse(
                        call: Call<PaymentResponse>,
                        response: Response<PaymentResponse>
                    ) {
                        _loading.value = false

                        if (response.isSuccessful && response.body() != null) {
                            val paymentResponse = response.body()!!
                            if (paymentResponse.success) {
                                // Lưu trữ orderId để tham chiếu sau này nếu cần
                                saveOrderId(context, paymentResponse.orderId)

                                _paymentUrl.value = paymentResponse.paymentUrl ?: ""

                                // Open the payment URL in browser
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(paymentResponse.paymentUrl)
                                )
                                context.startActivity(intent)
                            } else {
                                _error.value = paymentResponse.message
                            }
                        } else {
                            try {
                                _error.value = "Error: ${response.errorBody()?.string()}"
                            } catch (e: IOException) {
                                _error.value = "Unknown error occurred"
                            }
                        }
                    }

                    override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                        _loading.value = false
                        _error.value = "Network error: ${t.message}"
                        Log.e("PaymentViewModel", "Network error", t)
                    }
                })
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error: ${e.message}"
                Log.e("PaymentViewModel", "Error creating payment", e)
            }
        }
    }

    // Hàm lấy IP của thiết bị
    private fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                    0,
                                    delim
                                ).uppercase(Locale.getDefault())
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PaymentViewModel", "Error getting IP address", e)
        }
        return "localhost" // Fallback to localhost
    }

    // Trích xuất Base URL từ URL hoàn chỉnh
    private fun extractBaseUrl(url: String): String {
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val authority = uri.authority
        return "$scheme://$authority"
    }

    // Lưu OrderId để tham chiếu sau này
    private fun saveOrderId(context: Context, orderId: String?) {
        orderId?.let {
            context.getSharedPreferences("PaymentPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("LAST_ORDER_ID", it)
                .apply()
        }
    }

    // Kiểm tra trạng thái đăng ký
    fun checkSubscriptionStatus(userId: Long, context: Context) {
        _loading.value = true

        _subscriptionActive.value = true
        UserSharedPrefsHelper.setPremiumStatus(context, true)

        apiService.getSubscriptionStatus(userId).enqueue(object : Callback<SubscriptionResponse> {
            override fun onResponse(
                call: Call<SubscriptionResponse>,
                response: Response<SubscriptionResponse>
            ) {
                _loading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val subscription = response.body()!!
                    _subscriptionActive.value = subscription.active

                    // Save subscription status to SharedPreferences
                    UserSharedPrefsHelper.setPremiumStatus(context, subscription.active)
                } else {
                    _subscriptionActive.value = false
                    _error.value = "Failed to check subscription status"
                }
            }

            override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
                _loading.value = false
                _subscriptionActive.value = false
                _error.value = "Network error: ${t.message}"
                Log.e("PaymentViewModel", "Network error checking subscription", t)
            }
        })
    }
}