package com.example.kasku.ui

import androidx.lifecycle.lifecycleScope
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kasku.R
import com.example.kasku.utils.PdfExporter
import com.example.kasku.databinding.ActivityDashboardBinding
import com.example.kasku.ui.adapter.TransactionAdapter
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupGreeting()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupGreeting() {
        // Get username from SharedPreferences
        val prefs = getSharedPreferences("kasku_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "User") ?: "User"

        // Set username
        binding.tvUsername.text = username

        // Set greeting based on time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..10 -> "Selamat Pagi,"
            in 11..14 -> "Selamat Siang,"
            in 15..17 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }

        binding.tvGreeting.text = greeting
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    // Handle menu item click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_statistics -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_export_pdf -> {
                exportToPdf()
                true
            }
            R.id.action_reset -> {
                showResetConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportToPdf() {
        lifecycleScope.launch {
            // Gabungkan flow untuk mendapatkan snapshot data saat ini
            combine(
                viewModel.allTransactions,
                viewModel.totalIncome,
                viewModel.totalExpense
            ) { list, inc, exp ->
                Triple(list, inc ?: 0.0, exp ?: 0.0)
            }.first().let { (transactions, income, expense) ->

                val username = getSharedPreferences("kasku_prefs", MODE_PRIVATE)
                    .getString("username", "User") ?: "User"

                // Panggil exporter (sudah otomatis di background thread)
                val pdfExporter = PdfExporter(this@DashboardActivity)
                val file = pdfExporter.exportToPdf(
                    transactions,
                    income,
                    expense,
                    (income - expense),
                    username
                )

                // Kembali ke Main Thread untuk update UI
                if (file != null) {
                    AlertDialog.Builder(this@DashboardActivity)
                        .setTitle("Export Berhasil")
                        .setMessage("Laporan tersimpan di:\n${file.absolutePath}")
                        .setPositiveButton("Buka PDF") { _, _ ->
                            try {
                                // Mendapatkan URI file melalui FileProvider
                                val uri = FileProvider.getUriForFile(
                                    this@DashboardActivity,
                                    "$packageName.provider",
                                    file
                                )

                                // Membuat Intent untuk membuka PDF
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                }

                                // Jalankan Intent
                                startActivity(Intent.createChooser(intent, "Buka Laporan Dengan"))

                            } catch (e: Exception) {
                                Toast.makeText(this@DashboardActivity, "Tidak ada aplikasi PDF Reader", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("Tutup", null)
                        .show()
                }
            }
        }
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = this@DashboardActivity.adapter
        }
    }

    private fun setupListeners() {
        // Tombol Floating Action Button untuk tambah transaksi
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        // Observe daftar transaksi
        lifecycleScope.launch {
            viewModel.allTransactions.collect { transactions ->
                adapter.submitList(transactions)

                // Tampilkan empty state jika tidak ada transaksi
                if (transactions.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvTransactions.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvTransactions.visibility = View.VISIBLE
                }
            }
        }

        // Observe total pemasukan
        lifecycleScope.launch {
            viewModel.totalIncome.collect { income ->
                val totalIncome = income ?: 0.0
                binding.tvTotalIncome.text = formatCurrency(totalIncome)
                updateTotalBalance()
            }
        }

        // Observe total pengeluaran
        lifecycleScope.launch {
            viewModel.totalExpense.collect { expense ->
                val totalExpense = expense ?: 0.0
                binding.tvTotalExpense.text = formatCurrency(totalExpense)
                updateTotalBalance()
            }
        }
    }

    private fun updateTotalBalance() {
        lifecycleScope.launch {
            var income = 0.0
            var expense = 0.0

            viewModel.totalIncome.collect { inc ->
                income = inc ?: 0.0
                viewModel.totalExpense.collect { exp ->
                    expense = exp ?: 0.0
                    val balance = income - expense
                    binding.tvTotalBalance.text = formatCurrency(balance)
                    return@collect
                }
                return@collect
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount).replace("Rp", "Rp ")
    }

    // Tampilkan dialog konfirmasi reset
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.reset_confirmation_title)
            .setMessage(R.string.reset_confirmation_message)
            .setIcon(R.drawable.ic_delete_all)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                // User konfirmasi, hapus semua data
                resetAllData()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.reset_cancel) { dialog, _ ->
                // User cancel
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    // Fungsi untuk reset semua data
    private fun resetAllData() {
        viewModel.deleteAll()

        // Tampilkan pesan sukses
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Berhasil")
            .setMessage(R.string.reset_success)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Data akan otomatis terupdate karena menggunakan Flow/LiveData
        // yang bersifat observable
    }
}