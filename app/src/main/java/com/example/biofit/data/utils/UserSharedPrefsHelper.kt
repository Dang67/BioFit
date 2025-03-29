package com.example.biofit.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.biofit.data.model.dto.UserDTO
import com.google.gson.Gson

object UserSharedPrefsHelper {
    private const val PREF_NAME = "UserPrefs"
    private const val USER_KEY = "USER_DATA"
    private const val KEY_IS_PREMIUM = "is_premium"
    private const val KEY_SHOW_CONGRATS_DIALOG = "show_congrats_dialog"

    fun getUserData(context: Context): UserDTO? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(USER_KEY, null)

        return gson.fromJson(json, UserDTO::class.java)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getUserId(context: Context): Long {
        val userData = getUserData(context)
        return userData?.userId ?: 0L
    }

    fun setPremiumStatus(context: Context, isPremium: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_PREMIUM, isPremium)
            .apply()
    }

    fun setShowCongratulationsDialog(context: Context, show: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_SHOW_CONGRATS_DIALOG, show).apply()
    }

    fun shouldShowCongratulationsDialog(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_SHOW_CONGRATS_DIALOG, false)
    }

    fun clearShowCongratulationsDialog(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_SHOW_CONGRATS_DIALOG, false).apply()
    }
}