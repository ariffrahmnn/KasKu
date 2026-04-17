package com.example.kasku.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasku.data.api.RetrofitClient
import com.example.kasku.data.api.LoginResponse
import com.example.kasku.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama binding sesuai dengan nama file XML (activity_register.xml)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }

        // Jika user mengklik teks "Sudah punya akun? Login"
        binding.tvLoginLink.setOnClickListener {
            finish() // Menutup activity ini dan kembali ke LoginActivity
        }
    }

    private fun validateAndRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Reset error (asumsi kamu pakai TextInputLayout)
        binding.tilUsername.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        when {
            username.isEmpty() -> {
                binding.tilUsername.error = "Username wajib diisi"
                binding.etUsername.requestFocus()
            }
            email.isEmpty() -> {
                binding.tilEmail.error = "Email wajib diisi"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.tilPassword.error = "Password wajib diisi"
                binding.etPassword.requestFocus()
            }
            password.length < 6 -> {
                binding.tilPassword.error = "Password minimal 6 karakter"
                binding.etPassword.requestFocus()
            }
            else -> {
                processRegister(username, password, email)
            }
        }
    }

    private fun processRegister(user: String, pass: String, mail: String) {
        // Matikan tombol agar tidak diklik dua kali saat proses kirim data
        binding.btnRegister.isEnabled = false

        RetrofitClient.instance.registerUser(user, pass, mail).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.btnRegister.isEnabled = true

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success") {
                        Toast.makeText(this@RegisterActivity, body.message, Toast.LENGTH_LONG).show()
                        // Berhasil daftar, langsung balik ke halaman login
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, body?.message ?: "Gagal daftar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Server bermasalah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnRegister.isEnabled = true
                // Muncul jika XAMPP mati atau IP 10.0.2.2 salah
                Toast.makeText(this@RegisterActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}