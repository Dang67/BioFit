package com.example.biofit.data.model

import android.content.Context
import com.example.biofit.R
import com.example.biofit.data.model.dto.DailyLogDTO
import com.example.biofit.data.model.dto.UserDTO
import com.google.ai.client.generativeai.GenerativeModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChatMessage(val userMessage: String, val botResponse: String)

class ChatBotModel(
    private val userData: UserDTO,
    private val dailyLogData: DailyLogDTO,
    private val exerciseDone: List<String>?,
    private val context: Context,
    apiKey: String
) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey
    )
    private val chat = generativeModel.startChat()

    private val chatHistory = mutableListOf<ChatMessage>()
    private var isFirstConversation = true

    suspend fun getBotResponse(userInput: String): String {
        val recentHistory = chatHistory.takeLast(10)
        val conversationContext = recentHistory.joinToString("\n") {
            "User: ${it.userMessage}\nBot: ${it.botResponse}"
        }

        val userData = userData
        val dailyLogData = dailyLogData

        val enrichedInput = enrichInputWithUserData(userInput, userData, dailyLogData, exerciseDone)

        val fullConversation = if (conversationContext.isNotBlank()) {
            "$conversationContext\nUser: $enrichedInput"
        } else {
            enrichedInput
        }

        return try {
            val response = chat.sendMessage(fullConversation)
            val botReply = response.text
                ?.replace("*", "")
                ?: context.getString(R.string.sorry_i_don_t_understand)
            chatHistory.add(ChatMessage(userInput, botReply))
            botReply
        } catch (e: Exception) {
            context.getString(R.string.error_while_retrieving_data)
        }
    }

    private fun enrichInputWithUserData(
        userInput: String,
        userData: UserDTO,
        dailyLogData: DailyLogDTO,
        exerciseDone: List<String>?
    ): String {
        val introText = if (isFirstConversation) {
            isFirstConversation = false
            context.getString(R.string.bionix_tranning).trimIndent()
        } else {
            ""
        }
        return """ 
            $introText
            
            Current date time: ${
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    if (java.util.Locale.getDefault().language == "vi")
                        "EEEE, 'ngày' dd 'tháng' MM 'năm' yyyy, 'giờ' HH 'phút' mm"
                    else
                        "EEEE, MMMM d, yyyy, 'at' HH:mm"
                )
            )
        }
        
            [User data]:
                - Full Name: ${userData.fullName}
                - Gender: ${userData.getGenderString(context, userData.gender)}
                - Date of Birth: ${userData.dateOfBirth}
                - Height: ${userData.height} cm
                - Starting Weight: ${userData.weight} kg on ${userData.createdAccount}
                - Current Weight: ${dailyLogData.weight} kg
                - Drank ${dailyLogData.water} L of water today.
                - Target Weight: ${userData.targetWeight} kg
                - Completed Exercises: $exerciseDone
                
            [User input]: $userInput
        """.trimIndent()
    }
}
