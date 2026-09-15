package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.medisafe.app.data.model.Allergy
import com.medisafe.app.databinding.DialogEditAllergyBinding
import java.util.UUID

class EditAllergyDialog(
    context: Context,
    private val profileId: String,
    private val existing: Allergy?,
    private val onSave: (Allergy) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogEditAllergyBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        if (existing != null) {
            binding.tvAllergyDialogTitle.text = "Edit Allergy Record"
            binding.etAllergyDialogName.setText(existing.name)
            binding.etAllergyDialogType.setText(existing.type)
            binding.etAllergyDialogSeverity.setText(existing.severity)
            binding.etAllergyDialogReaction.setText(existing.reaction)
            binding.etAllergyDialogTreatment.setText(existing.currentTreatment)
            binding.cbIsCriticalAllergy.isChecked = existing.isCritical
        }

        binding.btnCancelAllergyDialog.setOnClickListener { dismiss() }

        binding.btnSaveAllergyDialog.setOnClickListener {
            val name = binding.etAllergyDialogName.text.toString().trim()
            val type = binding.etAllergyDialogType.text.toString().trim().ifEmpty { "Drug" }
            val severity = binding.etAllergyDialogSeverity.text.toString().trim().ifEmpty { "Moderate" }
            val reaction = binding.etAllergyDialogReaction.text.toString().trim()
            val treatment = binding.etAllergyDialogTreatment.text.toString().trim()
            val isCritical = binding.cbIsCriticalAllergy.isChecked

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter the allergen name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (reaction.isEmpty()) {
                Toast.makeText(context, "Please describe the allergy reaction", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val allergy = Allergy(
                id = existing?.id ?: ("alg_" + UUID.randomUUID().toString().take(8)),
                profileId = profileId,
                name = name,
                type = type,
                severity = severity,
                reaction = reaction,
                currentTreatment = treatment,
                isCritical = isCritical,
                status = existing?.status ?: "VERIFIED"
            )
            onSave(allergy)
            dismiss()
        }
    }
}
