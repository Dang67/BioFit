package com.example.biofit.data.model.dto

data class SocialAccountDTO(
    val accessToken: String,
    val refreshToken: String,
    val id: Long,
    val userId: Long,
    val provider: String,
    val avatarURL: String?,
    val providerId: String?,
    val createdAt: String?,
    val isNewUser: Boolean,
    val token: String
)