package com.example.kasku

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class KasKuApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Load Dark Mode preference
        val prefs = getSharedPreferences("kasku_prefs", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}