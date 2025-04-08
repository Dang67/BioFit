package com.example.biofit.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.example.biofit.R
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.remote.RetrofitClient
import com.example.biofit.data.utils.DailyLogSharedPrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyLogViewModel : ViewModel() {
    var weight = mutableStateOf<Float?>(null)
    var water = mutableStateOf<Float?>(null)
    var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    var saveState = mutableStateOf<Boolean?>(null)
    var saveMessage = mutableStateOf<String?>(null)

    fun updateWater(newValue: Float) {
        water.value = newValue  // Đảm bảo state luôn thay đổi
    }

    /*fun saveDailyLog(context: Context, userId: Long) {
        val apiService = RetrofitClient.instance

        val request = DailyLogDTO(
            dailyWeightId = null,
            userId = userId,
            weight = weight.value ?: memoryWeight.value,
            water = water.value ?: memoryWater.value,
            date = date
        )

        if (DailyLogSharedPrefsHelper.getDailyLog(context)?.date != date) {
            val oldLog = DailyLogSharedPrefsHelper.getDailyLog(context)

            val newWeight = if (weight.value != oldLog?.weight) weight.value else null
            val newWater = if (water.value != oldLog?.water) water.value else null

            if (newWeight == null && newWater == null) {
                saveState.value = false
                return
            }
            if (newWeight != null && newWater == null) {
                val requestWe = DailyLogDTO(
                    dailyWeightId = null,
                    userId = userId,
                    weight = newWeight,
                    water = 0f,
                    date = date
                )

                apiService.saveOrUpdateDailyWeight(requestWe)
                    .enqueue(object : Callback<DailyLogDTO> {
                        override fun onResponse(
                            call: Call<DailyLogDTO>,
                            response: Response<DailyLogDTO>
                        ) {
                            if (response.isSuccessful) {
                                val latestLog = response.body()
                                if (latestLog != null) {
                                    saveState.value = true
                                    saveMessage.value =
                                        context.getString(R.string.update_daily_weight_successfully)

                                    DailyLogSharedPrefsHelper.saveDailyLog(context, latestLog)
                                } else {
                                    saveState.value = false
                                    saveMessage.value = context.getString(R.string.update_failed)
                                }
                            } else {
                                saveState.value = false
                                saveMessage.value = context.getString(R.string.update_failed)
                            }
                        }

                        override fun onFailure(call: Call<DailyLogDTO>, t: Throwable) {
                            saveState.value = false
                            saveMessage.value =
                                context.getString(R.string.connection_error_please_try_again)
                        }
                    })
            }
            if (newWeight == null && newWater != null) {
                val requestWa = DailyLogDTO(
                    dailyWeightId = null,
                    userId = userId,
                    weight = memoryWeight.value,
                    water = newWater,
                    date = date
                )

                apiService.saveOrUpdateDailyWeight(requestWa)
                    .enqueue(object : Callback<DailyLogDTO> {
                        override fun onResponse(
                            call: Call<DailyLogDTO>,
                            response: Response<DailyLogDTO>
                        ) {
                            if (response.isSuccessful) {
                                val latestLog = response.body()
                                if (latestLog != null) {
                                    saveState.value = true
                                    saveMessage.value =
                                        context.getString(R.string.update_daily_weight_successfully)

                                    DailyLogSharedPrefsHelper.saveDailyLog(context, latestLog)
                                } else {
                                    saveState.value = false
                                    saveMessage.value = context.getString(R.string.update_failed)
                                }
                            } else {
                                saveState.value = false
                                saveMessage.value = context.getString(R.string.update_failed)
                            }
                        }

                        override fun onFailure(call: Call<DailyLogDTO>, t: Throwable) {
                            saveState.value = false
                            saveMessage.value =
                                context.getString(R.string.connection_error_please_try_again)
                        }
                    })
            }
        } else {
            apiService.saveOrUpdateDailyWeight(request).enqueue(object : Callback<DailyLogDTO> {
                override fun onResponse(call: Call<DailyLogDTO>, response: Response<DailyLogDTO>) {
                    if (response.isSuccessful) {
                        val latestLog = response.body()
                        if (latestLog != null) {
                            saveState.value = true
                            saveMessage.value =
                                context.getString(R.string.update_daily_weight_successfully)

                            DailyLogSharedPrefsHelper.saveDailyLog(context, latestLog)
                        } else {
                            saveState.value = false
                            saveMessage.value = context.getString(R.string.update_failed)
                        }
                    } else {
                        saveState.value = false
                        saveMessage.value = context.getString(R.string.update_failed)
                    }
                }

                override fun onFailure(call: Call<DailyLogDTO>, t: Throwable) {
                    saveState.value = false
                    saveMessage.value =
                        context.getString(R.string.connection_error_please_try_again)
                }
            })
        }
    }*/

    fun saveDailyLog(context: Context, userId: Long) {
        val apiService = RetrofitClient.instance
        val oldLog = DailyLogSharedPrefsHelper.getDailyLog(context)

        val newWeight = weight.value
        val newWater = water.value

        val finalWeight: Float
        val finalWater: Float

        if (newWeight != null && newWater == null) {
            // Chỉ cập nhật cân nặng → Water = 0
            finalWeight = newWeight
            finalWater = if (oldLog?.date == date) oldLog.water else 0f
        } else if (newWater != null && newWeight == null) {
            // Chỉ cập nhật lượng nước → Weight giữ nguyên giá trị cũ
            finalWeight = oldLog?.weight ?: 0f
            finalWater = newWater
        } else {
            // Cập nhật cả hai hoặc không cập nhật gì
            finalWeight = newWeight ?: oldLog?.weight ?: 0f
            finalWater = newWater ?: oldLog?.water ?: 0f
        }

        // Nếu log cũ đã tồn tại và không có thay đổi nào, không cần gọi API
        if (oldLog?.date == date && oldLog?.weight == finalWeight && oldLog.water == finalWater) {
            saveState.value = false
            return
        }

        // Cập nhật UI ngay lập tức để tránh cảm giác chậm
        saveState.value = true

        // Tạo request với dữ liệu mới
        val request = DailyLogDTO(
            dailyWeightId = null,
            userId = userId,
            weight = finalWeight,
            water = finalWater,
            date = date
        )

        // Gọi API và xử lý phản hồi
        apiService.saveOrUpdateDailyWeight(request).enqueue(object : Callback<DailyLogDTO> {
            override fun onResponse(call: Call<DailyLogDTO>, response: Response<DailyLogDTO>) {
                handleApiResponse(context, response)
            }

            override fun onFailure(call: Call<DailyLogDTO>, t: Throwable) {
                saveState.value = false
                saveMessage.value = context.getString(R.string.connection_error_please_try_again)
            }
        })
    }

    // Xử lý API response chung để tránh lặp code
    private fun handleApiResponse(context: Context, response: Response<DailyLogDTO>) {
        if (response.isSuccessful) {
            val latestLog = response.body()
            if (latestLog != null) {
                saveState.value = true
                saveMessage.value = context.getString(R.string.update_daily_weight_successfully)
                // 🔥 Cập nhật ViewModel
                water.value = latestLog.water
                Log.d("Water Updated", "Water: ${water.value}")

                // 🔥 Lưu vào SharedPreferences ngay lập tức
                DailyLogSharedPrefsHelper.saveDailyLog(context, latestLog)

                memoryWater.value = latestLog.water
            } else {
                saveState.value = false
                saveMessage.value = context.getString(R.string.update_failed)
            }
        } else {
            saveState.value = false
            saveMessage.value = context.getString(R.string.update_failed)
        }
    }

    var memoryWeight = mutableStateOf(0f)
    var memoryWater = mutableStateOf(0f)

    var getState = mutableStateOf<Boolean?>(null)

    fun getLatestDailyLog(context: Context, userId: Long) {
        if (userId == 0L) return

        val cachedLog = DailyLogSharedPrefsHelper.getDailyLog(context)
        if (cachedLog != null && cachedLog.userId == userId) {
            memoryWeight.value = cachedLog.weight
            memoryWater.value = cachedLog.water
            Log.d(
                "Memory Water Loaded",
                "Loaded from SharedPrefs: ${memoryWater.value}"
            ) // 🛠 Kiểm tra lại giá trị
        }

        val apiService = RetrofitClient.instance
        apiService.getLatestDailyLog(userId).enqueue(object : Callback<DailyLogDTO> {
            override fun onResponse(call: Call<DailyLogDTO>, response: Response<DailyLogDTO>) {
                if (response.isSuccessful) {
                    getState.value = true
                    val dailyLog = response.body()

                    memoryWeight.value = dailyLog?.weight ?: 0f
                    memoryWater.value = dailyLog?.water ?: 0f

                    Log.d(
                        "Memory Water API",
                        "Loaded from API: ${memoryWater.value}"
                    ) // 🛠 Kiểm tra giá trị từ API

                    dailyLog?.let {
                        DailyLogSharedPrefsHelper.saveDailyLog(context, it)
                        memoryWeight.value = it.weight
                        memoryWater.value = it.water
                        Log.d("Memory Water Updated from API", "Final Value: ${memoryWater.value}")
                    }
                } else {
                    getState.value = false
                }
            }

            override fun onFailure(call: Call<DailyLogDTO>, t: Throwable) {
                getState.value = false
            }
        })
    }

    var weightDataState = mutableStateOf<List<Pair<String, Float>>>(emptyList())
    var getWeightHistoryState = mutableStateOf<Boolean?>(null)

    fun getWeightHistory(userId: Long) {
        val apiService = RetrofitClient.instance

        val mainHandler = Handler(Looper.getMainLooper())
        apiService.getWeightHistory(userId).enqueue(object : Callback<List<DailyLogDTO>> {
            override fun onResponse(
                call: Call<List<DailyLogDTO>>,
                response: Response<List<DailyLogDTO>>
            ) {

                if (response.isSuccessful) {
                    val weightHistory = response.body()
                    if (weightHistory != null) {

                        val formatterDay = DateTimeFormatter.ofPattern("d")
                        val formatterMonth = DateTimeFormatter.ofPattern("M")

                        val weightData = weightHistory.map { weight ->
                            val localDate = LocalDate.parse(weight.date)
                            if (Locale.current.language == "vi") {
                                "${localDate.format(formatterDay)}/${localDate.format(formatterMonth)}" to weight.weight
                            } else {
                                "${localDate.format(formatterMonth)}/${localDate.format(formatterDay)}" to weight.weight
                            }
                        }

                        mainHandler.post {
                            weightDataState.value = weightData
                        }

                        getWeightHistoryState.value = true
                    } else {
                        getWeightHistoryState.value = false
                    }
                } else {
                    getWeightHistoryState.value = false
                }
            }


            override fun onFailure(call: Call<List<DailyLogDTO>>, t: Throwable) {
                getWeightHistoryState.value = false
            }
        })
    }
}