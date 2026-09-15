package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.AccessRequest
import com.medisafe.app.databinding.DialogAccessApprovalBinding

class AccessApprovalDialog(
    context: Context,
    private val request: AccessRequest,
    private val storedPin: String,
    private val onApproved: (durationMinutes: Int) -> Unit,
    private val onRejected: () -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAccessApprovalBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        binding.tvApprovalDoctorInfo.text = "${request.requesterName} (${request.requesterRole}) from ${request.organization} requests temporary access."
        binding.tvApprovalReason.text = "Reason: ${request.reason}"

        binding.btnRejectApproval.setOnClickListener {
            dismiss()
            onRejected()
        }

        binding.btnConfirmApproval.setOnClickListener {
            val enteredPin = binding.etApprovalPin.text.toString().trim()
            if (enteredPin != storedPin) {
                Toast.makeText(context, "Incorrect Emergency Access PIN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = when {
                binding.rb120min.isChecked -> 120
                binding.rb24hr.isChecked -> 1440
                else -> 30
            }

            dismiss()
            onApproved(duration)
        }
    }
}
