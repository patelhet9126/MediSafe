package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.EmergencyContact
import com.medisafe.app.databinding.DialogEditContactBinding
import java.util.UUID

class EditContactDialog(
    context: Context,
    private val profileId: String,
    private val existing: EmergencyContact?,
    private val onSave: (EmergencyContact) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogEditContactBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        if (existing != null) {
            binding.tvContactDialogTitle.text = "Edit Emergency Contact"
            binding.etContactDialogName.setText(existing.fullName)
            binding.etContactDialogRelation.setText(existing.relationship)
            binding.etContactDialogPhone.setText(existing.mobileNumber)
            binding.cbIsPrimaryContact.isChecked = existing.isPrimary
            binding.cbContactShowOnQr.isChecked = existing.showOnQr
        }

        binding.btnCancelContactDialog.setOnClickListener { dismiss() }

        binding.btnSaveContactDialog.setOnClickListener {
            val name = binding.etContactDialogName.text.toString().trim()
            val relation = binding.etContactDialogRelation.text.toString().trim().ifEmpty { "Other" }
            val phone = binding.etContactDialogPhone.text.toString().trim()
            val isPrimary = binding.cbIsPrimaryContact.isChecked
            val showOnQr = binding.cbContactShowOnQr.isChecked

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(context, "Please enter contact name and mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = EmergencyContact(
                id = existing?.id ?: ("emg_" + UUID.randomUUID().toString().take(8)),
                profileId = profileId,
                fullName = name,
                relationship = relation,
                mobileNumber = phone,
                isPrimary = isPrimary,
                priorityTier = if (isPrimary) "Primary" else "Secondary",
                showOnQr = showOnQr
            )
            onSave(contact)
            dismiss()
        }
    }
}
