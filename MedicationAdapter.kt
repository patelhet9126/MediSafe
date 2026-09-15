package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.Medication
import com.medisafe.app.databinding.ItemMedicationBinding

class MedicationAdapter(
    private var medications: List<Medication>,
    private val onEditClick: (Medication) -> Unit,
    private val onDeleteClick: (Medication) -> Unit
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    fun updateList(newList: List<Medication>) {
        medications = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(medications[position])
    }

    override fun getItemCount(): Int = medications.size

    inner class MedicationViewHolder(private val binding: ItemMedicationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(med: Medication) {
            binding.tvMedicationName.text = med.name
            binding.tvMedicationDosage.text = med.dosage
            binding.tvMedicationFrequency.text = med.frequency
            binding.tvMedicationReason.text = "Reason: ${med.reason}"
            binding.tvMedicationDoctor.text = if (med.prescribedBy.isNotBlank()) "Prescribed by ${med.prescribedBy}" else ""

            binding.btnEditMedication.setOnClickListener { onEditClick(med) }
            binding.btnDeleteMedication.setOnClickListener { onDeleteClick(med) }
        }
    }
}
