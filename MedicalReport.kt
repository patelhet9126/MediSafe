package com.medisafe.app.data.model

data class MedicalReport(
    val id: String,
    val profileId: String,
    val name: String,
    val type: String = "Blood Tests", // Blood Tests, X-Ray, MRI, CT Scan, ECG, Prescriptions, Discharge Summary, Surgery Reports, Other
    val uploadDate: String = "",
    val reportDate: String = "",
    val fileSize: String = "1.2 MB",
    val filePath: String = "",
    val hospital: String = "",
    val doctor: String = "",
    val relatedCondition: String = "",
    val status: String = "VERIFIED",
    val verifiedByPatient: Boolean = true,
    val notes: String = ""
)
