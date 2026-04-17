package com.example.kasku.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kasku.data.entity.Sale
import com.example.kasku.databinding.ActivitySalesBinding
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesBinding.inflate(layoutInflater)
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
                    this@SalesActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.etProductName.setAdapter(adapter)
                
                // Set default price if product matches
                binding.etProductName.setOnItemClickListener { _, _, position, _ ->
                    val selectedName = adapter.getItem(position)
                    val product = products.find { it.name == selectedName }
                    product?.let {
                        binding.etPrice.setText(it.sellingPrice.toString())
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
        val receipt = binding.etReceipt.text.toString().trim()
        val productName = binding.etProductName.text.toString().trim()
        val qtyText = binding.etQuantity.text.toString().trim()
        val priceText = binding.etPrice.text.toString().trim()
        val customer = binding.etCustomer.text.toString().trim()
        val cashier = binding.etCashier.text.toString().trim()
        
        val selectedId = binding.rgPaymentMethod.checkedRadioButtonId
        val paymentMethod = findViewById<RadioButton>(selectedId)?.text?.toString() ?: "Tunai"

        if (receipt.isEmpty() || productName.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            return
        }

        val qty = qtyText.toIntOrNull() ?: 0
        val price = priceText.toDoubleOrNull() ?: 0.0

        if (qty <= 0) {
            Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Gunakan ID default 1 karena tidak ada menu manajemen produk
        val sale = Sale(
            receiptNumber = receipt,
            productId = 1,
            productName = productName,
            quantity = qty,
            salePrice = price,
            paymentMethod = paymentMethod,
            customerInfo = customer,
            cashierName = cashier,
            timestamp = System.currentTimeMillis()
        )

        binding.btnSave.isEnabled = false
        viewModel.insertSale(sale) { success, message ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Penjualan $productName berhasil dicatat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, "Gagal: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}