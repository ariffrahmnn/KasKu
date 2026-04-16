package com.example.kasku.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasku.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Kredensial statis untuk demo
    private val VALID_USERNAME = "admin"
    private val VALID_PASSWORD = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            validateAndLogin()
        }

        // Tekan enter pada password juga bisa login
        binding.etPassword.setOnEditorActionListener { _, _, _ ->
            validateAndLogin()
            true
        }
    }

    private fun validateAndLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        when {
            username.isEmpty() -> {
                binding.tilUsername.error = "Username tidak boleh kosong"
                binding.etUsername.requestFocus()
            }
            password.isEmpty() -> {
                binding.tilPassword.error = "Password tidak boleh kosong"
                binding.etPassword.requestFocus()
            }
            username == VALID_USERNAME && password == VALID_PASSWORD -> {
                // Login berhasil - SIMPAN USERNAME
                getSharedPreferences("kasku_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("username", username)
                    .apply()

                Toast.makeText(this, "Login berhasil! Selamat datang", Toast.LENGTH_SHORT).show()

                // Pindah ke Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
                binding.etPassword.text?.clear()
                binding.etPassword.requestFocus()
            }
        }

        binding.tilUsername.error = null
        binding.tilPassword.error = null
    }
}