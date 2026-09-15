package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.MedicalCondition
import com.medisafe.app.databinding.DialogEditConditionBinding
import java.util.UUID

class EditConditionDialog(
    context: Context,
    private val profileId: String,
    private val existing: MedicalCondition?,
    private val onSave: (MedicalCondition) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogEditConditionBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        if (existing != null) {
            binding.tvConditionDialogTitle.text = "Edit Medical Condition"
            binding.etCondDialogName.setText(existing.name)
            binding.etCondDialogCategory.setText(existing.category)
            binding.etCondDialogDate.setText(existing.diagnosedDate)
            binding.etCondDialogHospital.setText(existing.hospital)
            binding.etCondDialogDoctor.setText(existing.doctor)
            binding.etCondDialogTreatment.setText(existing.treatment)
            binding.etCondDialogStents.setText(existing.stentsCount.toString())
            binding.etCondDialogStatus.setText(existing.statusDesc)
            binding.cbEmergencyRelevant.isChecked = existing.isEmergencyRelevant
            binding.etCondDialogNotes.setText(existing.notes)
        } else {
            binding.tvConditionDialogTitle.text = "+ Add Medical Condition"
        }

        binding.btnCancelCondDialog.setOnClickListener { dismiss() }

        binding.btnSaveCondDialog.setOnClickListener {
            val name = binding.etCondDialogName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter the medical condition name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = binding.etCondDialogCategory.text.toString().trim().ifEmpty { "Cardiovascular" }
            val date = binding.etCondDialogDate.text.toString().trim()
            val hospital = binding.etCondDialogHospital.text.toString().trim()
            val doctor = binding.etCondDialogDoctor.text.toString().trim()
            val treatment = binding.etCondDialogTreatment.text.toString().trim()
            val stents = binding.etCondDialogStents.text.toString().trim().toIntOrNull() ?: 0
            val statusDesc = binding.etCondDialogStatus.text.toString().trim()
            val isEmergency = binding.cbEmergencyRelevant.isChecked
            val notes = binding.etCondDialogNotes.text.toString().trim()

            val condition = MedicalCondition(
                id = existing?.id ?: ("cond_" + UUID.randomUUID().toString().take(8)),
                profileId = profileId,
                name = name,
                category = category,
                diagnosedDate = date,
                status = existing?.status ?: "VERIFIED",
                isCurrent = true,
                isEmergencyRelevant = isEmergency,
                hospital = hospital,
                doctor = doctor,
                treatment = treatment,
                stentsCount = stents,
                statusDesc = statusDesc,
                notes = notes
            )
            onSave(condition)
            dismiss()
        }
    }
}
