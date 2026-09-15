package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.MedicalCondition
import com.medisafe.app.databinding.ItemConditionBinding

class ConditionAdapter(
    private var conditions: List<MedicalCondition>,
    private val onViewClick: (MedicalCondition) -> Unit,
    private val onEditClick: (MedicalCondition) -> Unit,
    private val onDeleteClick: (MedicalCondition) -> Unit
) : RecyclerView.Adapter<ConditionAdapter.ConditionViewHolder>() {

    fun updateList(newList: List<MedicalCondition>) {
        conditions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConditionViewHolder {
        val binding = ItemConditionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConditionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConditionViewHolder, position: Int) {
        holder.bind(conditions[position])
    }

    override fun getItemCount(): Int = conditions.size

    inner class ConditionViewHolder(private val binding: ItemConditionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cond: MedicalCondition) {
            binding.tvConditionName.text = cond.name
            binding.tvConditionStatus.text = cond.status
            binding.tvConditionDetails.text = "${cond.category} • Diagnosed: ${cond.diagnosedDate}"
            binding.tvConditionHospital.text = "${cond.hospital} • ${cond.doctor}"
            binding.tvConditionTreatment.text = "Treatment: ${cond.treatment}"

            binding.btnViewCondition.setOnClickListener { onViewClick(cond) }
            binding.btnEditCondition.setOnClickListener { onEditClick(cond) }
            binding.btnDeleteCondition.setOnClickListener { onDeleteClick(cond) }
        }
    }
}
