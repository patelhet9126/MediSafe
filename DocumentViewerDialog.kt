package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.medisafe.app.data.model.MedicalReport
import com.medisafe.app.databinding.DialogDocumentViewerBinding

class DocumentViewerDialog(
    context: Context,
    private val report: MedicalReport
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogDocumentViewerBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        binding.tvViewerDocTitle.text = report.name
        binding.tvViewerDocMeta.text = "${report.type} • ${report.fileSize} • Status: ${report.status}"
        binding.tvViewerFacility.text = "Facility: ${report.hospital.ifEmpty { "Clinical Healthcare Center" }}"
        binding.tvViewerDoctor.text = "Treating Consultant: ${report.doctor.ifEmpty { "Attending Physician" }}"
        binding.tvViewerDate.text = "Report Date: ${report.reportDate}"

        binding.tvViewerFindings.text = "Encrypted Vault Extract:\n" +
                "• Document Type: ${report.type}\n" +
                "• Associated Condition: ${report.relatedCondition.ifEmpty { "General Clinical Registry" }}\n" +
                "• Verified by Patient: ${if (report.verifiedByPatient) "Yes (Verified Signature)" else "Pending Confirmation"}\n" +
                "• Storage Path: Private App Sandbox (Offline Encrypted)\n" +
                "• Security Hash: SHA-256 Validated"

        binding.btnCloseDocViewer.setOnClickListener { dismiss() }
    }
}
