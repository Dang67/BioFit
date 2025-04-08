package com.example.biofit.data.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.biofit.data.model.dto.DailyLogDTO
import com.google.gson.Gson

object DailyLogSharedPrefsHelper {
    private const val PREF_NAME = "DailyLogPrefs"
    private const val DAILY_LOG_KEY = "DAILY_LOG"

    fun saveDailyLog(context: Context, dailyLog: DailyLogDTO) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(dailyLog)

        editor.putString(DAILY_LOG_KEY, json)
        editor.apply()
        Log.d("SharedPrefs", "Saved Water: ${dailyLog.water}") // Kiểm tra lại
    }

    fun getDailyLog(context: Context): DailyLogDTO? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(DAILY_LOG_KEY, null)

        return gson.fromJson(json, DailyLogDTO::class.java)
    }

    fun clearDailyLog(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(DAILY_LOG_KEY).apply()
    }

}
