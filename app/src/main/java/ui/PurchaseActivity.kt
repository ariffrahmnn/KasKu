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

    private var selectedProductId: Int = 1

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Setup Spinner Unit
        val units = arrayOf("Pcs", "Box", "Lusin", "Kg", "Gram")
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units)
        binding.spinnerUnit.setAdapter(unitAdapter)

        // Observe products for AutoComplete suggestions
        lifecycleScope.launch {
            viewModel.allProducts.collectLatest { products ->
                val adapter = ArrayAdapter(
                    this@PurchaseActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.etProductName.setAdapter(adapter)

                binding.etProductName.setOnItemClickListener { _, _, _, _ ->
                    val selectedName = binding.etProductName.text.toString()
                    val product = products.find { it.name == selectedName }
                    product?.let {
                        selectedProductId = it.id
                        binding.etCategory.setText(it.category)
                        binding.spinnerUnit.setText(it.unit, false)
                        binding.etPurchasePrice.setText(it.purchasePrice.toString())
                        binding.etSellingPrice.setText(it.sellingPrice.toString())
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
        val category = binding.etCategory.text.toString().trim()
        val unit = binding.spinnerUnit.text.toString().trim()
        val qtyText = binding.etQuantity.text.toString().trim()
        val purchasePriceText = binding.etPurchasePrice.text.toString().trim()
        val sellingPriceText = binding.etSellingPrice.text.toString().trim()
        val extraCostText = binding.etExtraCost.text.toString().trim()

        if (invoice.isEmpty() || supplier.isEmpty() || productName.isEmpty() || 
            category.isEmpty() || qtyText.isEmpty() || purchasePriceText.isEmpty() || sellingPriceText.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data produk", Toast.LENGTH_SHORT).show()
            return
        }

        val qty = qtyText.toIntOrNull() ?: 0
        val purchasePrice = purchasePriceText.toDoubleOrNull() ?: 0.0
        val sellingPrice = sellingPriceText.toDoubleOrNull() ?: 0.0
        val extraCost = extraCostText.toDoubleOrNull() ?: 0.0

        if (qty <= 0) {
            Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val purchase = Purchase(
            invoiceNumber = invoice,
            purchaseDate = currentDate,
            supplierName = supplier,
            productId = selectedProductId,
            productName = productName,
            category = category,
            quantity = qty,
            unit = unit,
            pricePerUnit = purchasePrice,
            sellingPrice = sellingPrice,
            extraCost = extraCost,
            paymentStatus = "Lunas", // Selalu lunas sesuai request
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