package com.example.biofit.data.utils

import android.content.Context

object ChatPreferencesHelper {
    private const val PREF_NAME = "chat_prefs"
    private const val KEY_ANIMATED_MESSAGES = "animated_messages"

    fun markMessageAsAnimated(context: Context, message: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val messages = prefs.getStringSet(KEY_ANIMATED_MESSAGES, mutableSetOf()) ?: mutableSetOf()
        messages.add(message)

        prefs.edit().putStringSet(KEY_ANIMATED_MESSAGES, messages).apply()
    }

    fun hasMessageBeenAnimated(context: Context, message: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val messages = prefs.getStringSet(KEY_ANIMATED_MESSAGES, mutableSetOf()) ?: mutableSetOf()
        return messages.contains(message)
    }
}