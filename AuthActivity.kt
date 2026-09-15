package com.medisafe.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.medisafe.app.R
import com.medisafe.app.data.database.DatabaseHelper
import com.medisafe.app.databinding.ActivityAuthBinding
import com.medisafe.app.ui.MainActivity
import com.medisafe.app.utils.SecurityUtils

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var dbHelper: DatabaseHelper
    private var isRegisterMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecurityUtils.applyScreenProtection(this)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)

        setupToggle()
        setupListeners()
    }

    private fun setupToggle() {
        binding.tabRegister.setOnClickListener {
            if (!isRegisterMode) {
                isRegisterMode = true
                updateUIState()
            }
        }

        binding.tabLogin.setOnClickListener {
            if (isRegisterMode) {
                isRegisterMode = false
                updateUIState()
            }
        }
    }

    private fun updateUIState() {
        if (isRegisterMode) {
            binding.tabRegister.setBackgroundResource(R.drawable.bg_green_button)
            binding.tabRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tabLogin.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            binding.tvAuthTitle.text = "Create Health Account"
            binding.tilFullName.visibility = View.VISIBLE
            binding.tilMobile.visibility = View.VISIBLE
            binding.cbTerms.visibility = View.VISIBLE
            binding.btnSubmitAuth.text = "Proceed to OTP Verification"
        } else {
            binding.tabLogin.setBackgroundResource(R.drawable.bg_green_button)
            binding.tabLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tabRegister.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.tabRegister.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            binding.tvAuthTitle.text = "Sign In to Health Vault"
            binding.tilFullName.visibility = View.GONE
            binding.tilMobile.visibility = View.GONE
            binding.cbTerms.visibility = View.GONE
            binding.btnSubmitAuth.text = "Sign In"
        }
    }

    private fun setupListeners() {
        binding.btnAuthBack.setOnClickListener { finish() }

        binding.btnSubmitAuth.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRegisterMode) {
                val name = binding.etFullName.text.toString().trim()
                val mobile = binding.etMobile.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter your full legal name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (mobile.isEmpty() || mobile.length < 10) {
                    Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!binding.cbTerms.isChecked) {
                    Toast.makeText(this, "Please accept the medical triage terms", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Proceed to OTP Verification for new account
                val intent = Intent(this, OtpVerificationActivity::class.java).apply {
                    putExtra("EXTRA_FULL_NAME", name)
                    putExtra("EXTRA_EMAIL", email)
                    putExtra("EXTRA_MOBILE", mobile)
                    putExtra("EXTRA_PASSWORD", password)
                }
                startActivity(intent)
            } else {
                // Login Flow
                val result = dbHelper.loginUser(email, password)
                if (result.first) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("EXTRA_USER_ID", result.second)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, result.second, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
