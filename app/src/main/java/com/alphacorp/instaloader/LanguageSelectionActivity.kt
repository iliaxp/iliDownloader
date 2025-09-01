package com.alphacorp.instaloader

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.*

class LanguageSelectionActivity : AppCompatActivity() {
    
    private lateinit var btnEnglish: MaterialButton
    private lateinit var btnPersian: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("InstaLoaderPrefs", MODE_PRIVATE)
        
        // Check if language is already selected
        if (sharedPreferences.getBoolean("language_selected", false)) {
            navigateToNextScreen()
            return
        }
        
        initializeViews()
        setupAnimations()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        btnEnglish = findViewById(R.id.btnEnglish)
        btnPersian = findViewById(R.id.btnPersian)
    }
    
    private fun setupAnimations() {
        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        
        // Apply entrance animations
        btnEnglish.startAnimation(fadeInAnimation)
        btnPersian.startAnimation(fadeInAnimation)
        
        // Apply pulse animation to buttons
        btnEnglish.startAnimation(pulseAnimation)
        btnPersian.startAnimation(pulseAnimation)
    }
    
    private fun setupClickListeners() {
        btnEnglish.setOnClickListener {
            selectLanguage("en")
        }
        
        btnPersian.setOnClickListener {
            selectLanguage("fa")
        }
    }
    
    private fun selectLanguage(languageCode: String) {
        // Save language preference
        sharedPreferences.edit()
            .putString("selected_language", languageCode)
            .putBoolean("language_selected", true)
            .apply()
        
        // Set locale
        setLocale(languageCode)
        
        // Navigate to next screen
        navigateToNextScreen()
    }
    
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    
    private fun navigateToNextScreen() {
        // Check if permissions are already granted
        if (sharedPreferences.getBoolean("permissions_granted", false)) {
            // Go directly to main activity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Go to permission grant screen
            startActivity(Intent(this, PermissionGrantActivity::class.java))
        }
        finish()
    }
}
