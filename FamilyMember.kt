package com.medisafe.app.data.model

data class FamilyMember(
    val id: String,
    val profileId: String,
    val fullName: String,
    val relationship: String, // Father, Mother, Spouse, Child, Sibling, Other
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val photoUrl: String = "",
    val emergencyCardStatus: String = "Active",
    val profileCompletion: Int = 85,
    val primaryCondition: String = "No Chronic Illness",
    val emergencyContact: String = "",
    val allergiesCount: Int = 0,
    val recordsCount: Int = 0
)
