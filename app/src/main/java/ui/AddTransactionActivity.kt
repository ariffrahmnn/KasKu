package com.example.kasku.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kasku.data.entity.Transaction
import com.example.kasku.databinding.ActivityAddTransactionBinding
import com.example.kasku.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var viewModel: TransactionViewModel

    private var selectedDate: String = ""
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupListeners()
        setDefaultDate()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
    }

    private fun setupListeners() {
        // Date Picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.tilDate.setEndIconOnClickListener {
            showDatePicker()
        }

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun setDefaultDate() {
        selectedDate = dateFormat.format(calendar.time)
        binding.etDate.setText(displayDateFormat.format(calendar.time))
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = dateFormat.format(calendar.time)
                binding.etDate.setText(displayDateFormat.format(calendar.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun validateAndSave() {
        val amountText = binding.etAmount.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val type = if (binding.rbIncome.isChecked) "Pemasukan" else "Pengeluaran"

        // Validasi input
        var isValid = true

        if (amountText.isEmpty()) {
            binding.tilAmount.error = "Nominal tidak boleh kosong"
            isValid = false
        } else {
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Nominal harus lebih dari 0"
                isValid = false
            } else {
                binding.tilAmount.error = null
            }
        }

        if (description.isEmpty()) {
            binding.tilDescription.error = "Keterangan tidak boleh kosong"
            isValid = false
        } else {
            binding.tilDescription.error = null
        }

        if (selectedDate.isEmpty()) {
            binding.tilDate.error = "Tanggal harus dipilih"
            isValid = false
        } else {
            binding.tilDate.error = null
        }

        // Jika semua validasi berhasil, simpan data
        if (isValid) {
            val amount = amountText.toDouble()

            val transaction = Transaction(
                type = type,
                amount = amount,
                description = description,
                date = selectedDate
            )

            // Simpan ke database menggunakan ViewModel
            viewModel.insert(transaction)

            // Tampilkan pesan sukses
            Toast.makeText(
                this,
                "Transaksi berhasil disimpan",
                Toast.LENGTH_SHORT
            ).show()

            // Kembali ke Dashboard
            finish()
        } else {
            Toast.makeText(
                this,
                "Mohon lengkapi semua data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}