package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.Medication
import com.medisafe.app.databinding.DialogEditMedicationBinding
import java.util.UUID

class EditMedicationDialog(
    context: Context,
    private val profileId: String,
    private val existing: Medication?,
    private val onSave: (Medication) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogEditMedicationBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        if (existing != null) {
            binding.tvMedDialogTitle.text = "Edit Medication Record"
            binding.etMedDialogName.setText(existing.name)
            binding.etMedDialogDosage.setText(existing.dosage)
            binding.etMedDialogFrequency.setText(existing.frequency)
            binding.etMedDialogReason.setText(existing.reason)
            binding.etMedDialogDoctor.setText(existing.prescribedBy)
            binding.cbIsCriticalMed.isChecked = existing.isCritical
        }

        binding.btnCancelMedDialog.setOnClickListener { dismiss() }

        binding.btnSaveMedDialog.setOnClickListener {
            val name = binding.etMedDialogName.text.toString().trim()
            val dosage = binding.etMedDialogDosage.text.toString().trim()
            val frequency = binding.etMedDialogFrequency.text.toString().trim()
            val reason = binding.etMedDialogReason.text.toString().trim()
            val doctor = binding.etMedDialogDoctor.text.toString().trim()
            val isCritical = binding.cbIsCriticalMed.isChecked

            if (name.isEmpty() || dosage.isEmpty() || frequency.isEmpty()) {
                Toast.makeText(context, "Please enter medication name, dosage, and frequency", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val medication = Medication(
                id = existing?.id ?: ("med_" + UUID.randomUUID().toString().take(8)),
                profileId = profileId,
                name = name,
                dosage = dosage,
                frequency = frequency,
                reason = reason,
                prescribedBy = doctor,
                isCritical = isCritical,
                status = existing?.status ?: "VERIFIED"
            )
            onSave(medication)
            dismiss()
        }
    }
}
