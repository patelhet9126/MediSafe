package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.AccessRequest
import com.medisafe.app.databinding.ItemAccessRequestBinding

class AccessRequestAdapter(
    private var requests: List<AccessRequest>,
    private val onApproveClick: (AccessRequest) -> Unit,
    private val onRejectClick: (AccessRequest) -> Unit
) : RecyclerView.Adapter<AccessRequestAdapter.AccessRequestViewHolder>() {

    fun updateList(newList: List<AccessRequest>) {
        requests = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessRequestViewHolder {
        val binding = ItemAccessRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccessRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccessRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    inner class AccessRequestViewHolder(private val binding: ItemAccessRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(req: AccessRequest) {
            binding.tvAccessRequesterName.text = "${req.requesterName} • ${req.organization}"
            binding.tvAccessReason.text = "Reason: ${req.reason}"
            binding.tvAccessDuration.text = "Requested Session Duration: ${req.durationMinutes} minutes"
            binding.tvAccessStatus.text = req.status.uppercase()

            if (req.status.equals("pending", ignoreCase = true)) {
                binding.layoutAccessButtons.visibility = View.VISIBLE
                binding.btnApproveAccess.setOnClickListener { onApproveClick(req) }
                binding.btnRejectAccess.setOnClickListener { onRejectClick(req) }
            } else {
                binding.layoutAccessButtons.visibility = View.GONE
            }
        }
    }
}
