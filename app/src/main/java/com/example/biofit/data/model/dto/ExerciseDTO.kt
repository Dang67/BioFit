package com.example.biofit.data.model.dto

import android.content.Context
import android.os.Parcelable
import com.example.biofit.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseDTO(
    val exerciseId: Long,
    val userId: Long,
    val exerciseName: String,
    val detailList: List<ExerciseDetailDTO>
) : Parcelable {
    fun getExerciseGoalString(context: Context, exerciseGoal: Int): String {
        return when (exerciseGoal) {
            0 -> context.getString(R.string.amateur)
            1 -> context.getString(R.string.professional)
            else -> context.getString(R.string.exercise_goal_not_provided)
        }
    }

    fun getExerciseGoalInt(context: Context, exerciseGoal: String): Int {
        return when (exerciseGoal) {
            context.getString(R.string.amateur) -> 0
            context.getString(R.string.professional) -> 1
            else -> -1
        }
    }

    fun getIntensityString(context: Context, intensity: Int): String {
        return when (intensity) {
            0 -> context.getString(R.string.low)
            1 -> context.getString(R.string.medium)
            2 -> context.getString(R.string.high)
            else -> context.getString(R.string.intensity_not_provided)
        }
    }

    fun getIntensityInt(context: Context, intensity: String): Int {
        return when (intensity) {
            context.getString(R.string.low) -> 0
            context.getString(R.string.medium) -> 1
            context.getString(R.string.high) -> 2
            else -> -1
        }
    }

    companion object {
        fun default(): ExerciseDTO {
            return ExerciseDTO(
                exerciseId = 0,
                userId = 0,
                exerciseName = "N/A",
                detailList = emptyList()
            )
        }
    }
}