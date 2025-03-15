package com.example.biofit.view_model

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.biofit.R
import com.example.biofit.data.model.request.RegisterRequest
import com.example.biofit.data.model.dto.UserDTO
import com.example.biofit.data.remote.RetrofitClient
import com.example.biofit.ui.activity.InfoUserNameActivity
import com.example.biofit.ui.activity.RegisterSuccessfullyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RegisterViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    private var createdAccount = mutableStateOf(
        LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )
    )

    private val _userData = MutableStateFlow<UserDTO?>(null)
    val userData: StateFlow<UserDTO?> = _userData.asStateFlow()

    var registerState = mutableStateOf<Boolean?>(null)
    var registerMessage = mutableStateOf<String?>(null)

    fun registerUser(context: Context, loginViewModel: LoginViewModel) {
        val emailValidationMessage = validateEmail(context, email.value)
        if (emailValidationMessage != null) {
            registerState.value = false
            registerMessage.value = emailValidationMessage
            return
        }

        val passwordValidationMessage = validatePassword(context, password.value)
        if (passwordValidationMessage != null) {
            registerState.value = false
            registerMessage.value = passwordValidationMessage
            return
        }

        if (password.value != confirmPassword.value) {
            registerState.value = false
            registerMessage.value = context.getString(R.string.passwords_do_not_match)
            return
        }

        val apiService = RetrofitClient.instance

        val registerRequest = RegisterRequest(email.value, password.value, createdAccount.value)
        apiService.register(registerRequest).enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    registerState.value = true
                    registerMessage.value = context.getString(R.string.register_successfully)

                    user?.let {
                        _userData.value = it
                        loginViewModel.saveUserData(context, it)
                        val intent = Intent(context, RegisterSuccessfullyActivity::class.java)
                        context.startActivity(intent)
                    }
                } else if (response.code() == 409) {
                    registerState.value = false
                    registerMessage.value = context.getString(R.string.email_already_exists)
                } else {
                    registerState.value = false
                    registerMessage.value = context.getString(R.string.registration_failed)
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                registerState.value = false
                registerMessage.value =
                    context.getString(R.string.connection_error_please_try_again)
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

    private fun validatePassword(context: Context, password: String): String? {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return if (!password.matches(passwordPattern.toRegex())) {
            context.getString(R.string.password_must_contain_at_least_8_characters_one_uppercase_letter_one_lowercase_letter_and_one_number)
        } else {
            null
        }
    }
}