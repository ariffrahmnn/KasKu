package com.example.kasku.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kasku.R
import com.example.kasku.data.entity.Transaction
import com.example.kasku.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(
    TransactionDiffCallback()
) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.apply {
                // Set description
                tvDescription.text = transaction.description

                // Format dan set tanggal
                try {
                    val date = dateFormat.parse(transaction.date)
                    date?.let {
                        tvDate.text = displayDateFormat.format(it)
                    } ?: run {
                        tvDate.text = transaction.date
                    }
                } catch (e: Exception) {
                    tvDate.text = transaction.date
                }

                // Set amount dengan format mata uang
                val formattedAmount = currencyFormat.format(transaction.amount)
                    .replace("Rp", "Rp ")

                if (transaction.type == "Pemasukan") {
                    // Pemasukan - Warna Hijau
                    tvAmount.text = "+ $formattedAmount"
                    tvAmount.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.income)
                    )
                    ivType.setImageResource(R.drawable.ic_arrow_up)
                    ivType.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.income)
                    )
                    cardIcon.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.context, R.color.income_light)
                    )
                } else {
                    // Pengeluaran - Warna Merah
                    tvAmount.text = "- $formattedAmount"
                    tvAmount.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.expense)
                    )
                    ivType.setImageResource(R.drawable.ic_arrow_down)
                    ivType.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.expense)
                    )
                    cardIcon.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.context, R.color.expense_light)
                    )
                }
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}