package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.AuditLogEntry
import com.medisafe.app.databinding.ItemAuditLogBinding

class AuditLogAdapter(
    private var logs: List<AuditLogEntry>
) : RecyclerView.Adapter<AuditLogAdapter.AuditLogViewHolder>() {

    fun updateList(newList: List<AuditLogEntry>) {
        logs = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditLogViewHolder {
        val binding = ItemAuditLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuditLogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount(): Int = logs.size

    inner class AuditLogViewHolder(private val binding: ItemAuditLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: AuditLogEntry) {
            binding.tvAuditAction.text = log.action
            binding.tvAuditTimestamp.text = log.timestamp
            binding.tvAuditRequester.text = "${log.requesterName} • ${log.organization}"
            binding.tvAuditInfo.text = "Accessed: ${log.informationAccessed}"
        }
    }
}
