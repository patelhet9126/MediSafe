package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.FamilyMember
import com.medisafe.app.databinding.ItemFamilyMemberBinding

class FamilyMemberAdapter(
    private var members: List<FamilyMember>,
    private val onSwitchProfileClick: (FamilyMember) -> Unit
) : RecyclerView.Adapter<FamilyMemberAdapter.FamilyViewHolder>() {

    fun updateList(newList: List<FamilyMember>) {
        members = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyViewHolder {
        val binding = ItemFamilyMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FamilyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FamilyViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    inner class FamilyViewHolder(private val binding: ItemFamilyMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: FamilyMember) {
            binding.tvFamilyName.text = member.fullName
            binding.tvFamilyRelation.text = member.relationship.uppercase()
            binding.tvFamilyMeta.text = "Age: ${member.age} • Blood: ${member.bloodGroup} • ${member.emergencyCardStatus} Card"
            binding.tvFamilyCompletion.text = "${member.profileCompletion}%"
            binding.tvFamilyCondition.text = "Primary Condition: ${member.primaryCondition}"

            binding.btnSwitchToProfile.setOnClickListener { onSwitchProfileClick(member) }
        }
    }
}
