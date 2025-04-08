package com.example.biofit.data.model.dto

import android.os.Parcelable
import com.example.biofit.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodDTO(
    val foodId: Long,
    val userId: Long,
    val foodName: String,
    val session: String,
    val date: String,
    val foodImage: String,
    val servingSize: Float,
    val servingSizeUnit: String,
    val mass: Float,
    val calories: Float,
    val protein: Float,
    val carbohydrate: Float,
    val fat: Float,
    val sodium: Float
) : Parcelable {

    companion object {
        fun default(): FoodDTO {
            return FoodDTO(
                foodId = 0,
                userId = 0,
                foodName = "N/A",
                session = "morning",
                date = "",
                foodImage = "",
                servingSize = 0f,
                servingSizeUnit = "g",
                mass = 0f,
                calories = 0f,
                protein = 0f,
                carbohydrate = 0f,
                fat = 0f,
                sodium = 0f
            )
        }
    }
    // Chuyển đổi từ FoodDTO sang FoodInfo
    fun toFoodInfoDTO(): FoodInfoDTO {
        return FoodInfoDTO(
            foodId = this.foodId,
            foodImage = R.drawable.img_food_default,
            foodName = this.foodName,
            servingSize = Pair(this.servingSize, this.servingSizeUnit),
            mass = this.mass,
            calories = this.calories,
            protein = Triple(R.drawable.ic_protein, R.string.protein, this.protein),
            carbohydrate = Triple(R.drawable.ic_carbohydrate, R.string.carbohydrate, this.carbohydrate),
            fat = Triple(R.drawable.ic_fat, R.string.fat, this.fat),
            sodium = this.sodium,
            session = this.session
        )
    }
}
