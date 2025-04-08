package com.example.biofit.data.model.response

data class SubscriptionResponse(
    val id: Long,
    val userId: Long,
    val planType: String,
    val startDate: String,
    val endDate: String,
    val active: Boolean,
    val totalSubscriptionDays: Int
)