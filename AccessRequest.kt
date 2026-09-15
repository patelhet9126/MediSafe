package com.medisafe.app.data.model

data class AccessRequest(
    val id: String,
    val profileId: String,
    val requesterName: String,
    val requesterRole: String, // Doctor, Paramedic, Family Member, Caregiver
    val organization: String,
    val reason: String,
    val durationMinutes: Int = 30, // 30, 120, 1440
    val status: String = "pending", // pending, approved, rejected, expired, revoked
    val requestedAt: String = "",
    val expiresAt: String = "",
    val pinRequired: Boolean = true
)
