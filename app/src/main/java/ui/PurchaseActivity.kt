package com.example.kasku.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kasku.data.entity.Purchase
import com.example.kasku.databinding.ActivityPurchaseBinding
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Observe products for AutoComplete suggestions
        lifecycleScope.launch {
            viewModel.allProducts.collectLatest { products ->
                val adapter = ArrayAdapter(
                    this@PurchaseActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.etProductName.setAdapter(adapter)

                // Set default purchase price if product matches
                binding.etProductName.setOnItemClickListener { _, _, position, _ ->
                    val selectedName = adapter.getItem(position)
                    val product = products.find { it.name == selectedName }
                    product?.let {
                        binding.etPrice.setText(it.purchasePrice.toString())
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun validateAndSave() {
        val invoice = binding.etInvoice.text.toString().trim()
        val supplier = binding.etSupplier.text.toString().trim()
        val productName = binding.etProductName.text.toString().trim()
        val qtyText = binding.etQuantity.text.toString().trim()
        val priceText = binding.etPrice.text.toString().trim()
        val extraCostText = binding.etExtraCost.text.toString().trim()
        val status = if (binding.rbLunas.isChecked) "Lunas" else "Hutang"

        if (invoice.isEmpty() || supplier.isEmpty() || productName.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            return
        }

        val qty = qtyText.toIntOrNull() ?: 0
        val price = priceText.toDoubleOrNull() ?: 0.0
        val extraCost = extraCostText.toDoubleOrNull() ?: 0.0

        if (qty <= 0 || price <= 0) {
            Toast.makeText(this, "Jumlah dan Harga harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Gunakan ID default 1 karena tidak ada menu manajemen produk
        val purchase = Purchase(
            invoiceNumber = invoice,
            purchaseDate = currentDate,
            supplierName = supplier,
            productId = 1,
            productName = productName,
            quantity = qty,
            pricePerUnit = price,
            extraCost = extraCost,
            paymentStatus = status,
            timestamp = System.currentTimeMillis()
        )

        binding.btnSave.isEnabled = false
        viewModel.insertPurchase(purchase) { success, message ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Pembelian $productName berhasil dicatat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Gagal: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}