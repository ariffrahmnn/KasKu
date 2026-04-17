package com.example.kasku.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kasku.databinding.ActivityStockBinding
import com.example.kasku.ui.adapter.ProductAdapter
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

class StockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeData()
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

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@StockActivity)
            adapter = this@StockActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.allProducts.collect { products ->
                adapter.submitList(products)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProducts()
    }
}