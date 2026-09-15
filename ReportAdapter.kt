package com.medisafe.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medisafe.app.data.model.MedicalReport
import com.medisafe.app.databinding.ItemReportBinding

class ReportAdapter(
    private var reports: List<MedicalReport>,
    private val onViewClick: (MedicalReport) -> Unit,
    private val onEditClick: (MedicalReport) -> Unit,
    private val onReplaceClick: (MedicalReport) -> Unit,
    private val onDeleteClick: (MedicalReport) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    fun updateList(newList: List<MedicalReport>) {
        reports = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.size

    inner class ReportViewHolder(private val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(report: MedicalReport) {
            binding.tvReportName.text = report.name
            binding.tvReportMeta.text = "${report.type} • ${report.fileSize} • ${report.reportDate}"
            binding.tvReportHospital.text = "${report.hospital} • ${report.doctor}"
            binding.tvReportTypeBadge.text = report.status

            binding.btnViewReport.setOnClickListener { onViewClick(report) }
            binding.btnEditReport.setOnClickListener { onEditClick(report) }
            binding.btnReplaceReport.setOnClickListener { onReplaceClick(report) }
            binding.btnDeleteReport.setOnClickListener { onDeleteClick(report) }
        }
    }
}
