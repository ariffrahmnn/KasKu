package com.example.kasku.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kasku.R
import com.example.kasku.databinding.ActivityStatisticsBinding
import com.example.kasku.viewmodel.TransactionViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var viewModel: TransactionViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
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

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.allTransactions.collect { transactions ->
                if (transactions.isNotEmpty()) {
                    // Data untuk grafik
                    val incomeData = mutableListOf<Entry>()
                    val expenseData = mutableListOf<Entry>()
                    val dates = mutableListOf<String>()

                    // Group by date
                    val groupedByDate = transactions.groupBy { it.date }
                    val sortedDates = groupedByDate.keys.sorted()

                    sortedDates.forEachIndexed { index, date ->
                        val dayTransactions = groupedByDate[date] ?: emptyList()

                        val income = dayTransactions
                            .filter { it.type == "Pemasukan" }
                            .sumOf { it.amount }

                        val expense = dayTransactions
                            .filter { it.type == "Pengeluaran" }
                            .sumOf { it.amount }

                        incomeData.add(Entry(index.toFloat(), income.toFloat()))
                        expenseData.add(Entry(index.toFloat(), expense.toFloat()))

                        // Format tanggal untuk label
                        try {
                            val parsedDate = dateFormat.parse(date)
                            val displayFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                            dates.add(displayFormat.format(parsedDate ?: Date()))
                        } catch (e: Exception) {
                            dates.add(date)
                        }
                    }

                    setupLineChart(incomeData, expenseData, dates)
                    setupBarChart(incomeData, expenseData, dates)
                    setupPieChart(transactions)
                }
            }
        }
    }

    private fun setupLineChart(
        incomeData: List<Entry>,
        expenseData: List<Entry>,
        dates: List<String>
    ) {
        // Dataset untuk Pemasukan
        val incomeDataSet = LineDataSet(incomeData, "Pemasukan").apply {
            color = ContextCompat.getColor(this@StatisticsActivity, R.color.income)
            setCircleColor(ContextCompat.getColor(this@StatisticsActivity, R.color.income))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Dataset untuk Pengeluaran
        val expenseDataSet = LineDataSet(expenseData, "Pengeluaran").apply {
            color = ContextCompat.getColor(this@StatisticsActivity, R.color.expense)
            setCircleColor(ContextCompat.getColor(this@StatisticsActivity, R.color.expense))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(incomeDataSet, expenseDataSet)

        binding.lineChart.apply {
            data = lineData
            description.isEnabled = false
            legend.textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dates)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_secondary)
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_secondary)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${(value / 1000).toInt()}k"
                    }
                }
            }

            axisRight.isEnabled = false

            animateX(1000)
            invalidate()
        }
    }

    private fun setupBarChart(
        incomeData: List<Entry>,
        expenseData: List<Entry>,
        dates: List<String>
    ) {
        val incomeEntries = incomeData.map { BarEntry(it.x, it.y) }
        val expenseEntries = expenseData.map { BarEntry(it.x, it.y) }

        val incomeDataSet = BarDataSet(incomeEntries, "Pemasukan").apply {
            color = ContextCompat.getColor(this@StatisticsActivity, R.color.income)
            valueTextColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary)
        }

        val expenseDataSet = BarDataSet(expenseEntries, "Pengeluaran").apply {
            color = ContextCompat.getColor(this@StatisticsActivity, R.color.expense)
            valueTextColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary)
        }

        val barData = BarData(incomeDataSet, expenseDataSet)
        barData.barWidth = 0.35f

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            legend.textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dates)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_secondary)
                granularity = 1f
                setDrawGridLines(false)
                setCenterAxisLabels(true)
            }

            axisLeft.apply {
                textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_secondary)
            }

            axisRight.isEnabled = false

            groupBars(0f, 0.3f, 0f)
            animateY(1000)
            invalidate()
        }
    }

    private fun setupPieChart(transactions: List<com.example.kasku.data.entity.Transaction>) {
        val totalIncome = transactions
            .filter { it.type == "Pemasukan" }
            .sumOf { it.amount }

        val totalExpense = transactions
            .filter { it.type == "Pengeluaran" }
            .sumOf { it.amount }

        val entries = listOf(
            PieEntry(totalIncome.toFloat(), "Pemasukan"),
            PieEntry(totalExpense.toFloat(), "Pengeluaran")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(this@StatisticsActivity, R.color.income),
                ContextCompat.getColor(this@StatisticsActivity, R.color.expense)
            )
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.textColor = ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary)
            setEntryLabelColor(ContextCompat.getColor(this@StatisticsActivity, R.color.text_primary))
            animateY(1000)
            invalidate()
        }
    }
}