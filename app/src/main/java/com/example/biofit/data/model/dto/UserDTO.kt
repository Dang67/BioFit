package com.example.biofit.data.model.dto

import android.content.Context
import android.os.Parcelable
import com.example.biofit.R
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Parcelize
data class UserDTO(
    val userId: Long,
    val fullName: String?,
    val email: String,
    val gender: Int?,
    val height: Float?,
    val weight: Float?,
    val targetWeight: Float?,
    val dateOfBirth: String?,
    val avatar: String?,
    val createdAccount: String,
    val paymentStatus: String? = "BASIC"
) : Parcelable {
    fun getGenderString(context: Context, gender: Int?): String {
        return when (gender) {
            0 -> context.getString(R.string.male)
            1 -> context.getString(R.string.female)
            else -> context.getString(R.string.gender_not_provided)
        }
    }

    fun getGenderInt(context: Context, gender: String?): Int? {
        return when (gender) {
            context.getString(R.string.male) -> 0
            context.getString(R.string.female) -> 1
            else -> null
        }
    }

    fun getAge(context: Context, dateOfBirth: String?): String {
        if (dateOfBirth.isNullOrEmpty()) return context.getString(R.string.age_not_provided)

        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthDate = LocalDate.parse(dateOfBirth, formatter)
            val currentDate = LocalDate.now()
            context.getString(R.string.age) + ": " + Period.between(
                birthDate,
                currentDate
            ).years.toString()
        } catch (e: Exception) {
            context.getString(R.string.age_not_provided)
        }
    }

    fun getSubscriptionStatus(context: Context): String {
        return if (paymentStatus == "COMPLETED") "PRO" else "BASIC"
    }

    companion object {
        fun default(): UserDTO {
            return UserDTO(
                userId = 0,
                fullName = "N/A",
                email = "N/A",
                gender = 3,
                height = 0f,
                weight = 0f,
                targetWeight = 0f,
                dateOfBirth = "N/A",
                avatar = "N/A",
                createdAccount = "N/A",
                paymentStatus = "BASIC"
            )
        }
    }
}