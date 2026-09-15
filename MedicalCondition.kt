package com.medisafe.app.data.model

data class MedicalCondition(
    val id: String,
    val profileId: String,
    val name: String,
    val category: String = "Cardiovascular", // Cardiovascular, Respiratory, Endocrine, Allergy, Neurological, Other
    val diagnosedDate: String = "",
    val status: String = "VERIFIED", // VERIFIED, PENDING_VERIFICATION, NEEDS_REVIEW, DRAFT
    val isCurrent: Boolean = true,
    val isEmergencyRelevant: Boolean = false,
    val hospital: String = "",
    val city: String = "",
    val doctor: String = "",
    val treatment: String = "",
    val stentsCount: Int = 0,
    val graftCount: Int = 0,
    val statusDesc: String = "Currently taking medication",
    val symptomsDesc: String = "",
    val notes: String = ""
)
