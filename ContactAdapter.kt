package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.EmergencyContact
import com.medisafe.app.databinding.ItemContactBinding

class ContactAdapter(
    private var contacts: List<EmergencyContact>,
    private val onCallClick: (EmergencyContact) -> Unit,
    private val onEditClick: (EmergencyContact) -> Unit,
    private val onDeleteClick: (EmergencyContact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun updateList(newList: List<EmergencyContact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: EmergencyContact) {
            binding.tvContactName.text = contact.fullName
            binding.tvContactRelation.text = if (contact.isPrimary) "PRIMARY • ${contact.relationship.uppercase()}" else contact.relationship.uppercase()
            binding.tvContactPhone.text = contact.mobileNumber
            binding.tvContactQrStatus.text = if (contact.showOnQr) "✓ Displayed on Emergency QR Pass" else "Hidden from QR Triage"

            binding.btnCallContact.setOnClickListener { onCallClick(contact) }
            binding.btnEditContact.setOnClickListener { onEditClick(contact) }
            binding.btnDeleteContact.setOnClickListener { onDeleteClick(contact) }
        }
    }
}
