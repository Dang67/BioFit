package com.example.biofit.data.model.dto

import android.content.Context
import android.os.Parcelable
import com.example.biofit.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseDetailDTO(
    val exerciseDetailId: Long,
    val exerciseId: Long,
    val exerciseGoal: Int,
    val intensity: Int,
    val time: Float,
    val burnedCalories: Float
) : Parcelable {
    companion object {
        fun default(): ExerciseDetailDTO {
            return ExerciseDetailDTO(
                exerciseDetailId = 0,
                exerciseId = 0,
                exerciseGoal = 0,
                intensity = 0,
                time = 0f,
                burnedCalories = 0f
            )
        }
    }
}