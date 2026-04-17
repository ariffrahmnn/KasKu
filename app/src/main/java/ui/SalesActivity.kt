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

    private var selectedProductId: Int = 1 // Default ke 1 (Produk Umum)
    private var availableStock: Int = 0

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        
        lifecycleScope.launch {
            viewModel.allProducts.collectLatest { products ->
                val adapter = ArrayAdapter(
                    this@SalesActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.etProductName.setAdapter(adapter)
                
                binding.etProductName.setOnItemClickListener { _, _, _, _ ->
                    updateProductDetails(products)
                }

                // Tambahkan pengecekan saat fokus hilang (user selesai mengetik)
                binding.etProductName.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        updateProductDetails(products)
                    }
                }
            }
        }
    }

    private fun updateProductDetails(products: List<com.example.kasku.data.entity.Product>) {
        val selectedName = binding.etProductName.text.toString().trim()
        val product = products.find { it.name.equals(selectedName, ignoreCase = true) }
        product?.let {
            selectedProductId = it.id
            availableStock = it.stock
            binding.etPrice.setText(it.sellingPrice.toString())
            binding.tvStockInfo.text = "Stok tersedia: ${it.stock} ${it.unit}"
            binding.tvStockInfo.visibility = android.view.View.VISIBLE
        } ?: run {
            // Jika tidak ditemukan di database, anggap produk umum (ID 1)
            selectedProductId = 1
            availableStock = 999999 
            binding.tvStockInfo.visibility = android.view.View.GONE
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

        // Cek stok (jika bukan produk umum ID 1)
        if (selectedProductId != 1 && qty > availableStock) {
            Toast.makeText(this, "Stok tidak mencukupi! Tersedia: $availableStock", Toast.LENGTH_LONG).show()
            return
        }

        val sale = Sale(
            receiptNumber = receipt,
            productId = selectedProductId,
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