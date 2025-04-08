package com.example.biofit.data.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodDoneDTO(
    val foodDoneId: Long,
    val foodId: Long,
    val date: String,
    val session: String
) : Parcelable {
    companion object {
        fun default(): FoodDoneDTO {
            return FoodDoneDTO(
                foodDoneId = 0,
                foodId = 0,
                date = "",
                session = "morning"
            )
        }
    }
}
