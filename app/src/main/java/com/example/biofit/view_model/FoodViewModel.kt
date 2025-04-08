package com.example.biofit.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biofit.R
import com.example.biofit.data.model.dto.FoodDTO
import com.example.biofit.data.model.dto.FoodDoneDTO
import com.example.biofit.data.model.dto.FoodInfoDTO
import com.example.biofit.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FoodViewModel : ViewModel() {
    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    private val _food = MutableStateFlow<FoodDTO?>(null)
    val food: StateFlow<FoodDTO?> = _food

    private val _foodList = MutableStateFlow<List<FoodDTO>>(emptyList())
    val foodList: StateFlow<List<FoodDTO>> = _foodList.asStateFlow()

    // Phương thức lấy danh sách thực phẩm
    fun fetchFood(userId: Long) {
        val apiService = RetrofitClient.instance

        apiService.getFood(userId).enqueue(object : Callback<List<FoodDTO>> {
            override fun onResponse(call: Call<List<FoodDTO>>, response: Response<List<FoodDTO>>) {
                if (response.isSuccessful) {
                    val foodList = response.body() ?: emptyList()
                    _foodList.value = foodList

                    // Log the size of the food list and the actual data
                    Log.d("FoodViewModel", "Fetched food list size: ${foodList.size}")
                    Log.d("FoodViewModel", "Fetched food list: $foodList")
                } else {
                    Log.e("FoodViewModel", "API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FoodDTO>>, t: Throwable) {
                Log.e("FoodViewModel", "Network Error", t)
            }
        })
    }

    private val _foodListCreate = MutableStateFlow<List<FoodDTO>>(emptyList())
    val foodListCreate: StateFlow<List<FoodDTO>> = _foodListCreate.asStateFlow()

    private val _createdFood = MutableLiveData<FoodDTO?>()
    val createdFood: LiveData<FoodDTO?> get() = _createdFood

    fun createFood(foodDTO: FoodDTO) {
        val apiService = RetrofitClient.instance

        Log.d("FoodViewModel", "Creating food: $foodDTO") // ✅ Log dữ liệu gửi đi

        apiService.createFood(foodDTO).enqueue(object : Callback<FoodDTO> {
            override fun onResponse(call: Call<FoodDTO>, response: Response<FoodDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { newFood ->
                        _createdFood.value = newFood
                        Log.d("FoodViewModel", "Food created: $newFood")

                        val updatedList = _foodList.value.toMutableList()
                        updatedList.add(newFood) // Thêm món ăn mới vào danh sách
                        _foodList.value = updatedList // Cập nhật danh sách
                    }
                } else {
                    Log.e("FoodViewModel", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FoodDTO>, t: Throwable) {
                Log.e("FoodViewModel", "Failed to create food", t)
            }
        })
    }
    private val _createdFoodDone = MutableLiveData<FoodDoneDTO?>()
    val createdFoodDone: LiveData<FoodDoneDTO?> get() = _createdFoodDone

    private val _foodDoneList = MutableStateFlow<List<FoodDoneDTO>>(emptyList())
    val foodDoneList: StateFlow<List<FoodDoneDTO>> = _foodDoneList.asStateFlow()

    fun createFoodDone(foodDoneDTO: FoodDoneDTO) {
        Log.d("FoodViewModel", "Creating FoodDoneDTO: $foodDoneDTO")
        val apiService = RetrofitClient.instance

        apiService.createFoodDone(foodDoneDTO).enqueue(object : Callback<FoodDoneDTO> {
            override fun onResponse(
                call: Call<FoodDoneDTO>,
                response: Response<FoodDoneDTO>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { newFoodDone ->
                        _createdFoodDone.value = newFoodDone
                        Log.d("FoodViewModel", "Food created: $newFoodDone")

                        val updatedList = _foodDoneList.value.toMutableList()
                        updatedList.add(newFoodDone) // Thêm thực phẩm mới vào danh sách
                        _foodDoneList.value = updatedList // Cập nhật danh sách
                    }
                } else {
                    Log.e("FoodViewModel", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FoodDoneDTO>, t: Throwable) {
                Log.e("FoodViewModel", "Failed to create food done", t)
            }
        })
    }

    fun fetchFoodDoneList(userId: Long) {
        // Lấy ngày hiện tại dưới dạng chuỗi theo định dạng "yyyy-MM-dd"
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // Tạo đối tượng Retrofit API service
        val apiService = RetrofitClient.instance

        // Gọi API để lấy danh sách món ăn theo ngày và userId
        val call = apiService.getFoodDoneByDateAndUser(userId, currentDate)

        // Gửi yêu cầu API một cách bất đồng bộ
        call.enqueue(object : Callback<List<FoodDoneDTO>> {
            override fun onResponse(
                call: Call<List<FoodDoneDTO>>,
                response: Response<List<FoodDoneDTO>>
            ) {
                // Kiểm tra xem phản hồi có thành công không (mã trạng thái 200-299)
                if (response.isSuccessful) {
                    // Cập nhật danh sách món ăn đã ăn vào LiveData
                    _foodDoneList.value = response.body() ?: emptyList()
                    // Ghi thông tin vào Log (để kiểm tra dữ liệu trả về)
                    Log.d("FoodViewModel", "Fetched FoodDone for userId $userId and date $currentDate: ${response.body()}")
                } else {
                    // Nếu phản hồi không thành công, ghi lỗi vào Log
                    Log.e("FoodViewModel", "API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FoodDoneDTO>>, t: Throwable) {
                // Ghi lỗi mạng vào Log nếu có lỗi trong quá trình gọi API
                Log.e("FoodViewModel", "Network Error", t)
            }
        })
    }



    fun deleteFood(foodId: Long) {
        val apiService = RetrofitClient.instance

        apiService.deleteFood(foodId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("FoodViewModel", "Phản hồi xóa: $response")
                if (response.isSuccessful) {
                    Log.d("FoodViewModel", "Xóa món ăn thành công: $foodId")
                    val updatedList = _foodList.value.filterNot { it.foodId == foodId }
                    _foodList.value = updatedList.toList()
                    Log.d("FoodViewModel", "Đã xóa foodId: $foodId")
                } else {
                    Log.e("FoodViewModel", "Lỗi khi xóa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FoodViewModel", "Xóa thất bại", t)
            }
        })
    }


    fun updateFood(foodDTO: FoodDTO) {
        val apiService = RetrofitClient.instance

        // Gửi yêu cầu cập nhật món ăn với callback trả về kiểu FoodDTO
        apiService.updateFood(foodDTO.foodId, foodDTO).enqueue(object : Callback<FoodDTO> {
            override fun onResponse(call: Call<FoodDTO>, response: Response<FoodDTO>) {
                if (response.isSuccessful) {
                    // Nếu cập nhật thành công
                    Log.d("FoodViewModel", "Food updated successfully: ${foodDTO.foodName}")
                    // Xử lý logic sau khi cập nhật món ăn thành công, ví dụ: cập nhật UI, thông báo cho người dùng, v.v.
                } else {
                    Log.e("FoodViewModel", "Error updating food: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FoodDTO>, t: Throwable) {
                Log.e("FoodViewModel", "Failed to update food", t)
            }
        })
    }
}
