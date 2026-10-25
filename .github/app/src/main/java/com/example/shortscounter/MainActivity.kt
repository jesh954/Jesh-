package com.example.shortscounter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var countText: TextView
    private lateinit var limitLabel: TextView
    private lateinit var limitSeekBar: SeekBar
    private lateinit var strictSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("ShortsPrefs", Context.MODE_PRIVATE)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.parseColor("#0F0F0F"))
            setPadding(64, 120, 64, 64)
        }

        val titleText = TextView(this).apply {
            text = "Shorts Doomscroll Blocker"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }

        val cardBg = GradientDrawable().apply {
            cornerRadius = 36f
            setColor(Color.parseColor("#1E1E1E"))
        }

        countText = TextView(this).apply {
            textSize = 36f
            setTextColor(Color.parseColor("#FF453A"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            background = cardBg
            setPadding(64, 64, 64, 64)
        }

        val currentLimit = prefs.getInt("LIMIT", 20)
        limitLabel = TextView(this).apply {
            text = "Daily Limit: $currentLimit Shorts"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 72, 0, 24)
        }

        limitSeekBar = SeekBar(this).apply {
            max = 100
            progress = currentLimit
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                    val safeLimit = if (value < 5) 5 else value
                    limitLabel.text = "Daily Limit: $safeLimit Shorts"
                    prefs.edit().putInt("LIMIT", safeLimit).apply()
                    refreshStats()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        strictSwitch = Switch(this).apply {
            text = "Auto-Close YouTube at Limit"
            textSize = 16f
            setTextColor(Color.WHITE)
            isChecked = prefs.getBoolean("STRICT_MODE", true)
            setPadding(0, 48, 0, 64)
            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putBoolean("STRICT_MODE", isChecked).apply()
            }
        }

        val enableBtn = Button(this).apply {
            text = "1. Enable Accessibility Service"
            setBackgroundColor(Color.parseColor("#FF3B30"))
            setTextColor(Color.WHITE)
            setPadding(32, 32, 32, 32)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val resetBtn = Button(this).apply {
            text = "Reset Today's Count"
            setBackgroundColor(Color.parseColor("#2C2C2E"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                prefs.edit().putInt("COUNT", 0).apply()
                refreshStats()
            }
        }

        val spacer = Space(this).apply { minimumHeight = 32 }

        rootLayout.addView(titleText)
        rootLayout.addView(countText)
        rootLayout.addView(limitLabel)
        rootLayout.addView(limitSeekBar)
        rootLayout.addView(strictSwitch)
        rootLayout.addView(enableBtn)
        rootLayout.addView(spacer)
        rootLayout.addView(resetBtn)

        setContentView(rootLayout)
    }

    override fun onResume() {
        super.onResume()
        refreshStats()
    }

    private fun refreshStats() {
        val prefs = getSharedPreferences("ShortsPrefs", Context.MODE_PRIVATE)
        val count = prefs.getInt("COUNT", 0)
        val limit = prefs.getInt("LIMIT", 20)
        countText.text = "$count / $limit\nScrolled Today"
    }
}
