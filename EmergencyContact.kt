package com.medisafe.app.data.model

data class EmergencyContact(
    val id: String,
    val profileId: String,
    val fullName: String,
    val relationship: String,
    val mobileNumber: String,
    val alternateNumber: String = "",
    val email: String = "",
    val isPrimary: Boolean = false,
    val priorityTier: String = "Primary",
    val showOnQr: Boolean = true
)
