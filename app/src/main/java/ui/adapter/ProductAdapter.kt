package com.example.kasku.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kasku.data.entity.Product
import com.example.kasku.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvCategory.text = product.category
            binding.tvStock.text = "${product.stock} ${product.unit}"
            binding.tvPrice.text = "Jual: " + currencyFormat.format(product.sellingPrice).replace("Rp", "Rp ")
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }
}