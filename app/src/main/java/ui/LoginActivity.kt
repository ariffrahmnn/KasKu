package com.example.kasku.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kasku.databinding.ActivityLoginBinding
import com.example.kasku.data.api.RetrofitClient
import com.example.kasku.data.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Hapus atau biarkan VALID_USERNAME & VALID_PASSWORD (sudah tidak terpakai)

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

        // Navigasi ke Halaman Register
        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.etPassword.setOnEditorActionListener { _, _, _ ->
            validateAndLogin()
            true
        }
    }

    private fun validateAndLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Reset error
        binding.tilUsername.error = null
        binding.tilPassword.error = null

        when {
            username.isEmpty() -> {
                binding.tilUsername.error = "Username tidak boleh kosong"
                binding.etUsername.requestFocus()
            }
            password.isEmpty() -> {
                binding.tilPassword.error = "Password tidak boleh kosong"
                binding.etPassword.requestFocus()
            }
            else -> {
                // SEKARANG MEMANGGIL FUNGSI LOGIN API
                processLogin(username, password)
            }
        }
    }

    private fun processLogin(user: String, pass: String) {
        // Tampilkan loading (opsional) atau matikan tombol biar gak diklik berkali-kali
        binding.btnLogin.isEnabled = false

        RetrofitClient.instance.loginUser(user, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success") {
                        // Login Berhasil - Ambil data dari database
                        getSharedPreferences("kasku_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("username", user)
                            .apply()

                        Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login Gagal (Username/Pass salah di database)
                        Toast.makeText(this@LoginActivity, body?.message ?: "Login Gagal", Toast.LENGTH_SHORT).show()
                        binding.etPassword.text?.clear()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Server bermasalah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnLogin.isEnabled = true
                // Error koneksi (XAMPP mati atau IP salah)
                Toast.makeText(this@LoginActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}