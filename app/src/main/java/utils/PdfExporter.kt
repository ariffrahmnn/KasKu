package com.example.kasku.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.kasku.data.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PdfExporter(private val context: Context) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    suspend fun exportToPdf(
        transactions: List<Transaction>,
        totalIncome: Double,
        totalExpense: Double,
        totalBalance: Double,
        username: String
    ): File? = withContext(Dispatchers.IO) { // Pindah ke background thread
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            var pageNumber = 1

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            val paint = Paint()

            var yPos = 50f

            // --- Header ---
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("LAPORAN KEUANGAN", 50f, yPos, paint)

            yPos += 40f
            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("KasKu - Aplikasi Pencatat Keuangan", 50f, yPos, paint)

            yPos += 25f
            canvas.drawText("User: $username", 50f, yPos, paint)

            yPos += 20f
            val currentDate = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID")).format(Date())
            canvas.drawText("Tanggal Cetak: $currentDate", 50f, yPos, paint)

            yPos += 20f
            canvas.drawLine(50f, yPos, 545f, yPos, paint)

            // --- Ringkasan ---
            yPos += 35f
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("RINGKASAN", 50f, yPos, paint)

            yPos += 25f
            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("Total Pemasukan:", 50f, yPos, paint)
            canvas.drawText(formatCurrency(totalIncome), 300f, yPos, paint)

            yPos += 20f
            canvas.drawText("Total Pengeluaran:", 50f, yPos, paint)
            canvas.drawText(formatCurrency(totalExpense), 300f, yPos, paint)

            yPos += 20f
            paint.isFakeBoldText = true
            canvas.drawText("Saldo Akhir:", 50f, yPos, paint)
            canvas.drawText(formatCurrency(totalBalance), 300f, yPos, paint)

            yPos += 30f
            canvas.drawLine(50f, yPos, 545f, yPos, paint)

            // --- Tabel Transaksi ---
            yPos += 35f
            paint.textSize = 14f
            canvas.drawText("RIWAYAT TRANSAKSI", 50f, yPos, paint)

            yPos += 25f
            paint.textSize = 10f
            canvas.drawText("Tanggal", 50f, yPos, paint)
            canvas.drawText("Keterangan", 150f, yPos, paint)
            canvas.drawText("Jenis", 380f, yPos, paint)
            canvas.drawText("Nominal", 480f, yPos, paint)

            yPos += 10f
            canvas.drawLine(50f, yPos, 545f, yPos, paint)

            paint.isFakeBoldText = false
            transactions.sortedByDescending { it.timestamp }.forEach { transaction ->
                yPos += 25f

                // Cek apakah butuh halaman baru (Pagination)
                if (yPos > 780f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = 50f
                    
                    // Header Tabel di halaman baru agar tidak menimpa
                    paint.isFakeBoldText = true
                    canvas.drawText("Tanggal", 50f, yPos, paint)
                    canvas.drawText("Keterangan", 150f, yPos, paint)
                    canvas.drawText("Jenis", 380f, yPos, paint)
                    canvas.drawText("Nominal", 480f, yPos, paint)
                    
                    yPos += 10f
                    canvas.drawLine(50f, yPos, 545f, yPos, paint)
                    yPos += 25f
                    paint.isFakeBoldText = false
                }

                // Gambar Data
                val displayDate = try {
                    val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(transaction.date)
                    dateFormat.format(dateObj ?: Date())
                } catch (e: Exception) { transaction.date }

                canvas.drawText(displayDate, 50f, yPos, paint)

                // Truncate keterangan jika terlalu panjang
                val desc = if (transaction.description.length > 30) transaction.description.take(27) + "..." else transaction.description
                canvas.drawText(desc, 150f, yPos, paint)

                canvas.drawText(transaction.type, 380f, yPos, paint)

                val sign = if (transaction.type == "Pemasukan") "+" else "-"
                canvas.drawText("$sign ${formatCurrency(transaction.amount)}", 480f, yPos, paint)
            }

            pdfDocument.finishPage(page)

            // Simpan File
            val fileName = "Laporan_Keuangan_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount).replace("Rp", "Rp ")
    }
}