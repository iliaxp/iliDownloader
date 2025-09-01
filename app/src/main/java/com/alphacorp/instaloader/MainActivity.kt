package com.alphacorp.instaloader

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.SharedPreferences
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
import com.chaquo.python.PyObject
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var inputBox: TextInputEditText
    private lateinit var statusText: TextView
    private lateinit var downloadButton: MaterialButton
    private lateinit var progressSection: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var mainCard: View
    private lateinit var sharedPreferences: SharedPreferences
    
    private var isDownloading = false
    private var currentProgress = 0
    private var totalProgress = 100
    private var progressReceiverRegistered = false
    private val progressReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadService.ACTION_PROGRESS) {
                val current = intent.getIntExtra(DownloadService.EXTRA_CURRENT, 0)
                val total = intent.getIntExtra(DownloadService.EXTRA_TOTAL, 100)
                val status = intent.getStringExtra(DownloadService.EXTRA_STATUS) ?: ""
                val done = intent.getBooleanExtra(DownloadService.EXTRA_DONE, false)
                val failed = intent.getBooleanExtra(DownloadService.EXTRA_FAILED, false)
                runOnUiThread {
                    if (!progressSection.isShown) showProgressSection()
                    updateProgress(current, if (total == 0) 100 else total)
                    if (status.isNotBlank()) updateStatus(status)
                    if (done) {
                        if (failed) {
                            showToast(getString(R.string.toast_download_failed))
                        } else {
                            showToast(getString(R.string.toast_download_finished))
                        }
                        hideProgressSection()
                    }
                }
            }
        }
    }
    
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SharedPreferences and restore language
        sharedPreferences = getSharedPreferences("InstaLoaderPrefs", MODE_PRIVATE)
        restoreLanguage()
        
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupPermissions()
        setupPython()
        setupAnimations()
        setupClickListeners()
        registerProgressReceiver()
    }
    
    private fun restoreLanguage() {
        val selectedLanguage = sharedPreferences.getString("selected_language", "en")
        if (selectedLanguage != null) {
            setLocale(selectedLanguage)
        }
    }
    
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
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
            var input = inputBox.text?.toString() ?: ""
            if (input.isBlank()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData? = clipboard.primaryClip
                val text: CharSequence? = clip?.getItemAt(0)?.coerceToText(this)
                if (!text.isNullOrBlank()) {
                    inputBox.setText(text)
                    input = text.toString()
                    showToast(getString(R.string.toast_clipboard_pasted))
                }
            }
            if (input.isNotBlank()) {
                enqueueBackgroundDownload(input)
                inputBox.text?.clear()
            } else {
                showToast(getString(R.string.toast_empty_field))
                shakeInput()
            }
        }
    }
    
    private fun startDownload() {
        val input = inputBox.text.toString()
        isDownloading = true
        currentProgress = 0
        totalProgress = 100
        showToast(getString(R.string.toast_download_started))
        showProgressSection()
        updateProgress(10, 100)
        updateStatus(getString(R.string.progress_initializing))
        if (input.startsWith("https://www.instagram.com/")) {
            downloadFromLink(input)
        } else {
            downloadFromUsername(input)
        }
    }

    private fun enqueueBackgroundDownload(input: String) {
        val intent = Intent(this, DownloadService::class.java).apply {
            action = DownloadService.ACTION_START_DOWNLOAD
            putExtra(DownloadService.EXTRA_INPUT, input)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun registerProgressReceiver() {
        if (!progressReceiverRegistered) {
            val filter = IntentFilter(DownloadService.ACTION_PROGRESS)
            registerReceiver(progressReceiver, filter)
            progressReceiverRegistered = true
        }
    }

    override fun onDestroy() {
        if (progressReceiverRegistered) {
            unregisterReceiver(progressReceiver)
            progressReceiverRegistered = false
        }
        super.onDestroy()
    }
    
    private fun downloadFromLink(url: String) {
        val py = Python.getInstance()
        val module = py.getModule("script")
        val linkDownloader = module["download_post_from_link"]
        val getProgress = module["get_progress"]
        
        var postShortcode = ""
        if (url.startsWith("https://www.instagram.com/p/")) {
            postShortcode = url.substringAfter("https://www.instagram.com/p/").substringBefore("/")
        } else if (url.startsWith("https://www.instagram.com/reel/")) {
            postShortcode = url.substringAfter("https://www.instagram.com/reel/").substringBefore("/")
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Start progress monitoring
                startProgressMonitoring(getProgress)
                
                // Execute the actual download
                linkDownloader?.call(postShortcode)
                
                // Final progress update - Show "Finishing Download" and complete to 100%
                delay(1000)
                runOnUiThread {
                    updateProgress(100, 100)
                    updateStatus(getString(R.string.progress_finalizing))
                }
                
                // Small delay to show "Finishing Download" message
                delay(1500)
                
                runOnUiThread {
                    showToast(getString(R.string.toast_download_finished))
                    updateStatus(getString(R.string.progress_completed))
                    hideProgressSection()
                    showSuccessAnimation()
                    isDownloading = false
                    
                    // Clear input field for next download
                    inputBox.text?.clear()
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    showToast(getString(R.string.toast_download_failed))
                    updateStatus(getString(R.string.status_error, error.message ?: "Unknown error"))
                    hideProgressSection()
                    isDownloading = false
                }
            }
        }
    }
    
    private fun downloadFromUsername(username: String) {
        val py = Python.getInstance()
        val module = py.getModule("script")
        val downloader = module["download"]
        val posts = module["post_count"]
        val getProgress = module["get_progress"]
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get post count first
                val postCount = posts?.call(username) as? Int ?: 0
                
                runOnUiThread {
                    updateStatus(getString(R.string.status_found_posts, postCount))
                }
                
                if (postCount > 0) {
                    totalProgress = postCount
                    currentProgress = 0
                    
                    runOnUiThread {
                        updateProgress((postCount * 0.1).toInt(), postCount) // Show 10% progress after finding posts
                        updateStatus(getString(R.string.status_found_posts, postCount))
                    }
                    
                    // Start progress monitoring with post count
                    startProgressMonitoringWithTotal(getProgress, postCount)
                    
                    // Execute the download
                    downloader?.call(username)
                    
                    // Final progress update - Show "Finishing Download" and complete to 100%
                    delay(1000)
                    runOnUiThread {
                        updateProgress(postCount, postCount)
                        updateStatus(getString(R.string.progress_finalizing))
                    }
                    
                    // Small delay to show "Finishing Download" message
                    delay(1500)
                } else {
                    runOnUiThread {
                        updateProgress(0, 1)
                        updateStatus(getString(R.string.status_no_posts))
                    }
                }
                
                runOnUiThread {
                    showToast(getString(R.string.toast_download_finished))
                    updateStatus(getString(R.string.progress_completed))
                    hideProgressSection()
                    showSuccessAnimation()
                    isDownloading = false
                    
                    // Clear input field for next download
                    inputBox.text?.clear()
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    showToast(getString(R.string.toast_download_failed))
                    updateStatus(getString(R.string.status_error, error.message ?: "Unknown error"))
                    hideProgressSection()
                    isDownloading = false
                }
            }
        }
    }
    
    private fun startProgressMonitoring(getProgress: PyObject?) {
        startProgressMonitoringWithTotal(getProgress, 100)
    }
    
    private fun startProgressMonitoringWithTotal(getProgress: PyObject?, total: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var fakeProgress = (total * 0.1).toInt() // Start from 10% of total
            
            // Ensure we start at 10% immediately
            runOnUiThread {
                updateProgress(fakeProgress, total)
                updateStatus(getString(R.string.progress_downloading))
            }
            
            while (isDownloading) {
                try {
                    // Use fake progress that increments by 10% steps
                    val maxFakeProgress = (total * 0.8).toInt() // Go up to 80% of total to prevent getting stuck
                    if (fakeProgress < maxFakeProgress) {
                        fakeProgress += (total * 0.1).toInt() // Increment by 10% of total
                    }
                    
                    runOnUiThread {
                        updateProgress(fakeProgress, total)
                        updateStatus(getString(R.string.progress_downloading))
                    }
                    
                    delay(800) // Update every 800ms for smooth fake progress
                } catch (e: Exception) {
                    // Continue with fake progress even if Python call fails
                    val maxFakeProgress = (total * 0.8).toInt()
                    if (fakeProgress < maxFakeProgress) {
                        fakeProgress += (total * 0.1).toInt()
                    }
                    
                    runOnUiThread {
                        updateProgress(fakeProgress, total)
                        updateStatus(getString(R.string.progress_downloading))
                    }
                    delay(800)
                }
            }
        }
        }
    
    private fun updateProgress(current: Int, max: Int) {
        currentProgress = current
        totalProgress = max
        
        val percentage = if (max > 0) ((current.toFloat() / max.toFloat()) * 100).toInt() else 0
        progressBar.max = max
        progressBar.progress = current
        progressText.text = "$percentage%"
        
        // Add smooth animation for progress updates
        progressBar.animate()
            .setDuration(200)
            .start()
    }
    
    private fun showProgressSection() {
        progressSection.visibility = View.VISIBLE
        progressSection.alpha = 0f
        progressSection.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        // Start with initial progress
        updateProgress(0, totalProgress)
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

