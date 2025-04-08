package com.example.biofit.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.biofit.R
import com.example.biofit.data.model.ChatBotModel
import com.example.biofit.data.model.ChatMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AIChatbotViewModel(
    private val model: ChatBotModel,
    private val context: Context
) : ViewModel() {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _chatHistory = mutableStateListOf<ChatMessage>()
    val chatHistory: List<ChatMessage> get() = _chatHistory

    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    init {
        loadChatHistory() // Tải lịch sử khi ViewModel được khởi tạo
    }

    fun sendMessage(userInput: String, scope: CoroutineScope) {
        if (userInput.isBlank()) return

        _chatHistory.add(ChatMessage(userInput, context.getString(R.string.composing_a_message)))
        _isLoading.value = true
        saveChatHistory()

        scope.launch {
            val response = model.getBotResponse(userInput)
            _chatHistory[_chatHistory.lastIndex] = ChatMessage(userInput, response)
            _isLoading.value = false
            saveChatHistory()
        }
    }

    private fun saveChatHistory() {
        val chatJson = gson.toJson(_chatHistory)
        sharedPrefs.edit().putString("chat_history", chatJson).apply()
    }

    private fun loadChatHistory() {
        val chatJson = sharedPrefs.getString("chat_history", null)
        if (chatJson != null) {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            val savedHistory: List<ChatMessage> = gson.fromJson(chatJson, type)
            _chatHistory.addAll(savedHistory)
        }
    }

    fun clearChatHistory() {
        sharedPrefs.edit().remove("chat_history").apply()
        _chatHistory.clear()
    }
}
