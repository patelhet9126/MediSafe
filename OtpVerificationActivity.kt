package com.medisafe.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.medisafe.app.databinding.ActivityOtpBinding
import com.medisafe.app.ui.onboarding.OnboardingActivity
import com.medisafe.app.utils.SecurityUtils

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecurityUtils.applyScreenProtection(this)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mobile = intent.getStringExtra("EXTRA_MOBILE") ?: "+91 98765 43210"
        binding.tvOtpSubtitle.text = "We sent a 6-digit verification code to $mobile."

        setupOtpTextWatcher()

        binding.btnOtpBack.setOnClickListener { finish() }

        binding.btnVerifyOtp.setOnClickListener {
            val code = "${binding.etOtp1.text}${binding.etOtp2.text}${binding.etOtp3.text}${binding.etOtp4.text}${binding.etOtp5.text}${binding.etOtp6.text}"
            if (code.length < 6) {
                Toast.makeText(this, "Please enter all 6 digits of the OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Mobile number verified successfully!", Toast.LENGTH_SHORT).show()

            // Proceed to Profile Setup / Onboarding Flow
            val intent = Intent(this, OnboardingActivity::class.java).apply {
                putExtra("EXTRA_FULL_NAME", intent.getStringExtra("EXTRA_FULL_NAME") ?: "Aarav Patel")
                putExtra("EXTRA_EMAIL", intent.getStringExtra("EXTRA_EMAIL") ?: "aarav.patel@example.com")
                putExtra("EXTRA_MOBILE", mobile)
                putExtra("EXTRA_PASSWORD", intent.getStringExtra("EXTRA_PASSWORD") ?: "SecurePass#2026")
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupOtpTextWatcher() {
        binding.etOtp1.addTextChangedListener { if (it?.length == 1) binding.etOtp2.requestFocus() }
        binding.etOtp2.addTextChangedListener { if (it?.length == 1) binding.etOtp3.requestFocus() }
        binding.etOtp3.addTextChangedListener { if (it?.length == 1) binding.etOtp4.requestFocus() }
        binding.etOtp4.addTextChangedListener { if (it?.length == 1) binding.etOtp5.requestFocus() }
        binding.etOtp5.addTextChangedListener { if (it?.length == 1) binding.etOtp6.requestFocus() }
    }
}
