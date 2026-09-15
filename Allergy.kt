package com.medisafe.app.data.model

data class Allergy(
    val id: String,
    val profileId: String,
    val type: String = "Drug", // Drug, Food, Environmental, Insect, Latex, Other
    val name: String,
    val allergen: String = "",
    val reaction: String,
    val severity: String = "Critical / Life Threatening", // Critical / Life Threatening, Severe, Moderate, Mild
    val sinceWhen: String = "",
    val currentTreatment: String = "",
    val isCritical: Boolean = false,
    val status: String = "VERIFIED",
    val notes: String = ""
)
