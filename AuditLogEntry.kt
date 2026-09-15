package com.medisafe.app.data.model

data class AuditLogEntry(
    val id: String,
    val profileId: String,
    val action: String, // Viewed, Requested, Approved, Rejected, Revoked, Record Created, Record Updated, Record Deleted
    val requesterName: String,
    val requesterRole: String,
    val organization: String,
    val informationAccessed: String,
    val status: String = "Authorized",
    val timestamp: String = "Just now"
)
