package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.FamilyMember
import com.medisafe.app.databinding.DialogAddFamilyMemberBinding
import java.util.UUID

class AddFamilyDialog(
    context: Context,
    private val onSave: (FamilyMember) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAddFamilyMemberBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        binding.btnCancelFamilyDialog.setOnClickListener { dismiss() }

        binding.btnSaveFamilyDialog.setOnClickListener {
            val name = binding.etFamilyDialogName.text.toString().trim()
            val relation = binding.etFamilyDialogRelation.text.toString().trim().ifEmpty { "Child" }
            val age = binding.etFamilyDialogAge.text.toString().trim().toIntOrNull() ?: 10
            val blood = binding.etFamilyDialogBlood.text.toString().trim().ifEmpty { "O+" }
            val gender = binding.etFamilyDialogGender.text.toString().trim().ifEmpty { "Male" }
            val condition = binding.etFamilyDialogCondition.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter family member's name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newProfileId = "prof_fam_" + UUID.randomUUID().toString().take(8)
            val member = FamilyMember(
                id = "fam_" + UUID.randomUUID().toString().take(8),
                profileId = newProfileId,
                fullName = name,
                relationship = relation,
                age = age,
                gender = gender,
                bloodGroup = blood,
                photoUrl = "",
                emergencyCardStatus = "Active",
                profileCompletion = 85,
                primaryCondition = condition.ifEmpty { "No Chronic Illness" },
                emergencyContact = "+91 98765 43210 (Primary Guardian)"
            )
            onSave(member)
            dismiss()
        }
    }
}
