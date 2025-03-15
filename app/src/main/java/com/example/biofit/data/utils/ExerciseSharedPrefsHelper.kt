package com.example.biofit.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.biofit.data.model.dto.ExerciseDTO
import com.google.gson.Gson

object ExerciseSharedPrefsHelper {
    private const val PREF_NAME = "ExercisePrefs"
    private const val EXERCISE_KEY = "EXERCISE"

    fun saveExercise(context: Context, exercise: ExerciseDTO) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(exercise)

        editor.putString(EXERCISE_KEY, json)
        editor.apply()
    }

    fun getExercise(context: Context): ExerciseDTO? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(EXERCISE_KEY, null)

        return gson.fromJson(json, ExerciseDTO::class.java)
    }

    fun clearExercise(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(EXERCISE_KEY).apply()
    }
}