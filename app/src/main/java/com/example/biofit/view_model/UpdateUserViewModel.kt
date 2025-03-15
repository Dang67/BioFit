package com.example.biofit.view_model

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.biofit.R
import com.example.biofit.data.model.request.UpdateUserRequest
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.remote.RetrofitClient
import com.example.biofit.ui.screen.base64ToBitmap
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class UpdateUserViewModel : ViewModel() {
    var fullName = mutableStateOf<String?>(null)
    var gender = mutableStateOf<Int?>(null)
    var email = mutableStateOf<String?>(null)
    var dateOfBirth = mutableStateOf<String?>(null)
    var height = mutableStateOf<Float?>(null)
    var weight = mutableStateOf<Float?>(null)
    var targetWeight = mutableStateOf<Float?>(null)
    /*var avatar = mutableStateOf<String?>(null)*/
    var avatarBitmap = mutableStateOf<Bitmap?>(null) // Thay vì String, lưu Bitmap
    var updatedState = mutableStateOf<Boolean?>(null)
    var updatedMessage = mutableStateOf<String?>(null)

    fun setAvatar(bitmap: Bitmap) {
        avatarBitmap.value = bitmap
    }

    private fun getAvatarAsBase64(): String? {
        avatarBitmap.value?.let { bitmap ->
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        }
        return null
    }

    fun updateUser(
        context: Context,
        userId: Long,
        loginViewModel: LoginViewModel,
        onSuccess: () -> Unit
    ) {
        val emailValue = email.value
        if (emailValue != null) { // Chỉ validate khi có email
            val emailValidationMessage = validateEmail(context, emailValue)
            if (emailValidationMessage != null) {
                updatedState.value = false
                updatedMessage.value = emailValidationMessage
                return
            }
        }

        val apiService = RetrofitClient.instance

        // Chuyển đổi avatarBitmap thành MultipartBody.Part
        val avatarPart: MultipartBody.Part? = avatarBitmap.value?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val requestBody = stream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody)
        }

        val updateUserRequest = UpdateUserRequest(
            fullName.value,
            email.value,
            gender.value,
            dateOfBirth.value,
            height.value,
            weight.value,
            targetWeight.value,
            /*avatar.value*/
        )
        val userRequestBody = Gson().toJson(updateUserRequest)
            .toRequestBody("application/json".toMediaTypeOrNull())

        apiService.updateUser(userId, avatarPart, /*updateUserRequest*/ userRequestBody).enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    updatedState.value = true

                    user?.let { updatedUser ->
                        fullName.value = updatedUser.fullName
                        email.value = updatedUser.email
                        gender.value = updatedUser.gender
                        dateOfBirth.value = updatedUser.dateOfBirth
                        height.value = updatedUser.height
                        weight.value = updatedUser.weight
                        targetWeight.value = updatedUser.targetWeight
                        /*avatar.value = updatedUser.avatar*/
                        avatarBitmap.value = base64ToBitmap(updatedUser.avatar)
                        Log.d("UpdateUser", "Updated avatar Base64: ${updatedUser.avatar?.take(100)}...")

                        loginViewModel.saveUserData(context, updatedUser)

                        onSuccess()
                    }
                } else if (response.code() == 403) {
                    updatedState.value = false
                    updatedMessage.value = context.getString(R.string.email_already_exists)
                } else {
                    updatedState.value = false
                    updatedMessage.value = "Failed to update user"
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                updatedState.value = false
                updatedMessage.value = context.getString(R.string.connection_error_please_try_again)
            }
        })
    }

    private fun validateEmail(context: Context, email: String): String? {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return if (!email.matches(emailPattern.toRegex())) {
            context.getString(R.string.please_enter_a_valid_email)
        } else {
            null
        }
    }
}
