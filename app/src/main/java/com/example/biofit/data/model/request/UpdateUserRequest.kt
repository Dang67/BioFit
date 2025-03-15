package com.example.biofit.data.model.request

data class UpdateUserRequest(
    val fullName: String?,
    val email: String?,
    val gender: Int?,
    val dateOfBirth: String?,
    val height: Float?,
    val weight: Float?,
    val targetWeight: Float?
)