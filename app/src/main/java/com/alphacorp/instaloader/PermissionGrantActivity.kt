package com.alphacorp.instaloader

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class PermissionGrantActivity : AppCompatActivity() {
    
    private lateinit var btnGrantPermission: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 101
        private const val POST_NOTIFICATIONS_REQUEST_CODE = 102
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_grant)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("InstaLoaderPrefs", MODE_PRIVATE)
        
        // Check if permissions are already granted
        if (sharedPreferences.getBoolean("permissions_granted", false)) {
            navigateToMainActivity()
            return
        }
        
        initializeViews()
        setupAnimations()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        btnGrantPermission = findViewById(R.id.btnGrantPermission)
    }
    
    private fun setupAnimations() {
        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        
        // Apply entrance animations
        btnGrantPermission.startAnimation(fadeInAnimation)
        
        // Apply pulse animation to button
        btnGrantPermission.startAnimation(pulseAnimation)
    }
    
    private fun setupClickListeners() {
        btnGrantPermission.setOnClickListener {
            requestPermissions()
        }
    }
    
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+), request MANAGE_EXTERNAL_STORAGE
            if (!android.os.Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_CODE)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_CODE)
                }
            } else {
                permissionsGranted()
            }
        } else {
            // For Android 10 and below, request WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                permissionsGranted()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    POST_NOTIFICATIONS_REQUEST_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted()
                } else {
                    showPermissionDeniedMessage()
                }
            }
            POST_NOTIFICATIONS_REQUEST_CODE -> { /* no-op */ }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            MANAGE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (android.os.Environment.isExternalStorageManager()) {
                        permissionsGranted()
                    } else {
                        showPermissionDeniedMessage()
                    }
                }
            }
        }
    }
    
    private fun permissionsGranted() {
        // Save permission status
        sharedPreferences.edit()
            .putBoolean("permissions_granted", true)
            .apply()
        
        Toast.makeText(this, "Permissions granted successfully!", Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }
    
    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this, 
            "Storage permission is required to download Instagram content. Please grant the permission.", 
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
