package com.example.biofit.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.biofit.data.model.dto.UserDTO
import com.google.gson.Gson

object UserSharedPrefsHelper {
    private const val PREF_NAME = "UserPrefs"
    private const val USER_KEY = "USER_DATA"
    private const val KEY_IS_PREMIUM = "is_premium"

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
        getPrefs(context).edit().putBoolean(KEY_IS_PREMIUM, isPremium).apply()
    }
}