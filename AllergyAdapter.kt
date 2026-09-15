package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.Allergy
import com.medisafe.app.databinding.ItemAllergyBinding

class AllergyAdapter(
    private var allergies: List<Allergy>,
    private val onEditClick: (Allergy) -> Unit,
    private val onDeleteClick: (Allergy) -> Unit
) : RecyclerView.Adapter<AllergyAdapter.AllergyViewHolder>() {

    fun updateList(newList: List<Allergy>) {
        allergies = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val binding = ItemAllergyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllergyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        holder.bind(allergies[position])
    }

    override fun getItemCount(): Int = allergies.size

    inner class AllergyViewHolder(private val binding: ItemAllergyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(allergy: Allergy) {
            binding.tvAllergyName.text = allergy.name
            binding.tvAllergySeverity.text = allergy.severity.uppercase()
            binding.tvAllergyReaction.text = "Reaction: ${allergy.reaction}"
            binding.tvAllergyTreatment.text = "Protocol: ${allergy.currentTreatment}"

            binding.btnEditAllergy.setOnClickListener { onEditClick(allergy) }
            binding.btnDeleteAllergy.setOnClickListener { onDeleteClick(allergy) }
        }
    }
}
