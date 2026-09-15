package com.medisafe.app.data.model

data class Medication(
    val id: String,
    val profileId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val reason: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val prescribedBy: String = "",
    val isCritical: Boolean = false,
    val status: String = "VERIFIED",
    val notes: String = ""
)
