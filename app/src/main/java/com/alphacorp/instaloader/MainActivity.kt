package com.alphacorp.instaloader

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Removed invalid shake property declaration

class MainActivity : AppCompatActivity() {
    
    private lateinit var inputBox: TextInputEditText
    private lateinit var statusText: TextView
    private lateinit var downloadButton: MaterialButton
    private lateinit var progressSection: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var mainCard: View
    
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupPermissions()
        setupPython()
        setupAnimations()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        inputBox = findViewById(R.id.inputBox)
        statusText = findViewById(R.id.StatusText)
        downloadButton = findViewById(R.id.button)
        progressSection = findViewById(R.id.progressSection)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        mainCard = findViewById(R.id.mainCard)
    }
    
    private fun setupPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf<String>(WRITE_EXTERNAL_STORAGE), 1)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }
    
    private fun setupPython() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
    
    private fun setupAnimations() {
        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        
        // Apply entrance animations with staggered timing
        mainCard.alpha = 0f
        mainCard.translationY = 100f
        
        mainCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(android.view.animation.OvershootInterpolator())
            .start()
        
        // Apply pulse animation to download button
        downloadButton.startAnimation(pulseAnimation)
    }
    
    private fun setupClickListeners() {
        downloadButton.setOnClickListener {
            if (inputBox.text.toString().isNotEmpty()) {
                startDownload()
            } else {
                showToast(getString(R.string.toast_empty_field))
                shakeInput()
            }
        }
    }
    
    private fun startDownload() {
        val input = inputBox.text.toString()
        showToast(getString(R.string.toast_download_started))
        
        // Show progress section with animation
        showProgressSection()
        
        if (input.startsWith("https://www.instagram.com/")) {
            downloadFromLink(input)
        } else {
            downloadFromUsername(input)
        }
    }
    
    private fun downloadFromLink(url: String) {
        val py = Python.getInstance()
        val module = py.getModule("script")
        val linkDownloader = module["download_post_from_link"]
        
        var postShortcode = ""
        if (url.startsWith("https://www.instagram.com/p/")) {
            postShortcode = url.substringAfter("https://www.instagram.com/p/").substringBefore("/")
        } else if (url.startsWith("https://www.instagram.com/reel/")) {
            postShortcode = url.substringAfter("https://www.instagram.com/reel/").substringBefore("/")
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate progress for single post download
                simulateProgress(100)
                
                linkDownloader?.call(postShortcode)
                
                runOnUiThread {
                    showToast("Download Finished")
                    updateStatus("Download completed successfully!")
                    hideProgressSection()
                    showSuccessAnimation()
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    showToast("Download failed")
                    updateStatus("Error: ${error.message}")
                    hideProgressSection()
                }
            }
        }
    }
    
    private fun downloadFromUsername(username: String) {
        val py = Python.getInstance()
        val module = py.getModule("script")
        val downloader = module["download"]
        val posts = module["post_count"]
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postCount = posts?.call(username) as? Int ?: 0
                
                runOnUiThread {
                    updateStatus("Found $postCount posts, Downloading...")
                }
                
                // Simulate progress based on post count
                simulateProgress(postCount * 10)
                
                downloader?.call(username)
                
                runOnUiThread {
                    showToast("Download Finished")
                    updateStatus("Download completed successfully!")
                    hideProgressSection()
                    showSuccessAnimation()
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    showToast("Download failed")
                    updateStatus("Error: ${error.message}")
                    hideProgressSection()
                }
            }
        }
    }
    
    private suspend fun simulateProgress(maxProgress: Int) {
        var progress = 0
        while (progress < maxProgress) {
            progress += (maxProgress / 20).coerceAtLeast(1)
            runOnUiThread {
                updateProgress(progress, maxProgress)
            }
            delay(100) // Simulate download time
        }
    }
    
    private fun updateProgress(current: Int, max: Int) {
        val percentage = ((current.toFloat() / max.toFloat()) * 100).toInt()
        progressBar.max = max
        progressBar.progress = current
        progressText.text = "$percentage%"
        
        // Update status text based on progress
        when {
            percentage < 25 -> updateStatus("Initializing download...")
            percentage < 50 -> updateStatus("Downloading content...")
            percentage < 75 -> updateStatus("Processing media files...")
            percentage < 100 -> updateStatus("Finalizing download...")
            else -> updateStatus("Download completed!")
        }
    }
    
    private fun showProgressSection() {
        progressSection.visibility = View.VISIBLE
        progressSection.alpha = 0f
        progressSection.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun hideProgressSection() {
        progressSection.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                progressSection.visibility = View.GONE
            }
            .start()
    }
    
    private fun updateStatus(message: String) {
        statusText.text = message
        statusText.alpha = 0f
        statusText.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun shakeInput() {
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        inputBox.startAnimation(shakeAnimation)
    }
    
    private fun showSuccessAnimation() {
        // Create a beautiful success animation
        downloadButton.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .withEndAction {
                downloadButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
        
        // Add a success color flash
        val originalBackground = downloadButton.background
        downloadButton.setBackgroundColor(resources.getColor(R.color.success, null))
        downloadButton.postDelayed({
            downloadButton.background = originalBackground
        }, 500)
    }
}

