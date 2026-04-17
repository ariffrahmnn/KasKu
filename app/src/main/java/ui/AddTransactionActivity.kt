package com.example.kasku.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kasku.databinding.ActivityAddTransactionBinding
import com.example.kasku.viewmodel.TransactionViewModel

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupListeners()
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
        binding.btnSaveProduct.setOnClickListener {
            validateAndSave()
        }
    }

    private fun validateAndSave() {
        val name = binding.etProductName.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val stockText = binding.etStock.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val purchasePriceText = binding.etPurchasePrice.text.toString().trim()
        val sellingPriceText = binding.etSellingPrice.text.toString().trim()

        if (name.isEmpty() || category.isEmpty() || stockText.isEmpty() || 
            unit.isEmpty() || purchasePriceText.isEmpty() || sellingPriceText.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val stock = stockText.toIntOrNull() ?: 0
        val purchasePrice = purchasePriceText.toDoubleOrNull() ?: 0.0
        val sellingPrice = sellingPriceText.toDoubleOrNull() ?: 0.0

        binding.btnSaveProduct.isEnabled = false
        Toast.makeText(this, "Menyimpan produk...", Toast.LENGTH_SHORT).show()

        viewModel.addProduct(name, category, stock, unit, purchasePrice, sellingPrice) { success, message ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Produk berhasil ditambahkan ke stok", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSaveProduct.isEnabled = true
                    Toast.makeText(this, "Gagal: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}