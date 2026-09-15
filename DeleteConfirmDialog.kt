package com.medisafe.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.medisafe.app.databinding.DialogDeleteConfirmBinding

class DeleteConfirmDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val onConfirmed: () -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogDeleteConfirmBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        binding.tvDeleteTitle.text = title
        binding.tvDeleteMessage.text = message

        binding.btnCancelDelete.setOnClickListener {
            dismiss()
        }

        binding.btnConfirmDelete.setOnClickListener {
            dismiss()
            onConfirmed()
        }
    }
}
