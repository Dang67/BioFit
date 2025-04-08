package com.example.biofit.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.biofit.data.model.dto.OverviewExerciseDTO
import com.google.gson.Gson

object OverviewExerciseSharedPrefsHelper {
    private const val PREF_NAME = "OverviewExercisePrefs"
    private const val OVERVIEW_EXERCISE_KEY = "OVERVIEW_EXERCISE"

    fun saveListOverviewExercise(context: Context, overviewExerciseDTO: List<OverviewExerciseDTO>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(overviewExerciseDTO)

        editor.putString(OVERVIEW_EXERCISE_KEY, json)
        editor.apply()
    }

    fun getListOverviewExercise(context: Context): List<OverviewExerciseDTO>? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(OVERVIEW_EXERCISE_KEY, null)

        return gson.fromJson(json, Array<OverviewExerciseDTO>::class.java)?.toList()
    }

    fun clearListOverviewExercise(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(OVERVIEW_EXERCISE_KEY).apply()
    }
}