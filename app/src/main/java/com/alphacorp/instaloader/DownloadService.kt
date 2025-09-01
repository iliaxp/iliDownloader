package com.alphacorp.instaloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class DownloadService : Service() {

	companion object {
		const val ACTION_START_DOWNLOAD: String = "com.alphacorp.instaloader.action.START_DOWNLOAD"
		const val ACTION_PROGRESS: String = "com.alphacorp.instaloader.action.PROGRESS"
		const val EXTRA_INPUT: String = "extra_input"
		const val EXTRA_CURRENT: String = "extra_current"
		const val EXTRA_TOTAL: String = "extra_total"
		const val EXTRA_STATUS: String = "extra_status"
		const val EXTRA_DONE: String = "extra_done"
		const val EXTRA_FAILED: String = "extra_failed"
		private const val CHANNEL_ID: String = "downloads"
		private const val CHANNEL_NAME: String = "Background Downloads"
		private const val FOREGROUND_ID: Int = 1001
	}

	private val serviceScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
	private val isProcessing: AtomicBoolean = AtomicBoolean(false)
	private val pendingInputs: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
	@Volatile private var progressLoopActive: Boolean = false

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
		startForeground(FOREGROUND_ID, buildIndeterminateNotification(getString(R.string.notification_preparing)))
		initializePython()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (intent?.action == ACTION_START_DOWNLOAD) {
			val input: String? = intent.getStringExtra(EXTRA_INPUT)
			if (!input.isNullOrBlank()) {
				pendingInputs.add(input)
				processQueue()
			}
		}
		return START_STICKY
	}

	private fun processQueue() {
		if (isProcessing.compareAndSet(false, true)) {
			serviceScope.launch { runQueue() }
		}
	}

	private suspend fun runQueue() {
		while (true) {
			val next: String? = pendingInputs.poll()
			if (next == null) break
			updateNotification(buildIndeterminateNotification(getString(R.string.notification_connecting)))
			try {
				startProgressLoop()
				performDownload(next)
				updateNotification(buildCompletedNotification())
				sendProgressBroadcast(1, 1, getString(R.string.notification_completed), done = true, failed = false)
			} catch (_: Throwable) {
				updateNotification(buildFailedNotification())
				sendProgressBroadcast(0, 1, getString(R.string.notification_failed), done = true, failed = true)
			}
			stopProgressLoop()
		}
		isProcessing.set(false)
		if (pendingInputs.isEmpty()) stopSelf()
	}

	private fun performDownload(input: String) {
		val py = Python.getInstance()
		val module = py.getModule("script")
		val getProgress = module["get_progress"]
		if (input.startsWith("https://www.instagram.com/")) {
			val linkDownloader = module["download_post_from_link"]
			val shortcode: String = when {
				input.startsWith("https://www.instagram.com/p/") -> input.substringAfter("https://www.instagram.com/p/").substringBefore("/")
				input.startsWith("https://www.instagram.com/reel/") -> input.substringAfter("https://www.instagram.com/reel/").substringBefore("/")
				else -> ""
			}
			updateNotification(buildProgressNotification(10, 100, getString(R.string.notification_downloading_post)))
			linkDownloader?.call(shortcode)
		} else {
			val downloader = module["download"]
			updateNotification(buildProgressNotification(10, 100, getString(R.string.notification_downloading_profile)))
			downloader?.call(input)
		}
	}

	private fun startProgressLoop() {
		progressLoopActive = true
		serviceScope.launch {
			try {
				val py = Python.getInstance()
				val module = py.getModule("script")
				val getProgress = module["get_progress"]
				while (progressLoopActive) {
					try {
						val progressObj = getProgress?.call()
						val mapAny: Map<*, *> = (progressObj?.asMap() as? Map<*, *>) ?: emptyMap<Any, Any>()
						val current = (mapAny["current"] as? Int) ?: 0
						val total = (mapAny["total"] as? Int) ?: 0
						val status = (mapAny["status"] as? String) ?: ""
						if (total > 0) {
							updateNotification(buildProgressNotification(current, total, status.ifBlank { getString(R.string.notification_connecting) }))
						}
						sendProgressBroadcast(current, total, status, done = false, failed = false)
						Thread.sleep(800)
					} catch (_: Throwable) {
						Thread.sleep(800)
					}
				}
			} catch (_: Throwable) { }
		}
	}

	private fun stopProgressLoop() {
		progressLoopActive = false
	}

	private fun sendProgressBroadcast(current: Int, total: Int, status: String, done: Boolean, failed: Boolean) {
		val intent = Intent(ACTION_PROGRESS)
		intent.putExtra(EXTRA_CURRENT, current)
		intent.putExtra(EXTRA_TOTAL, total)
		intent.putExtra(EXTRA_STATUS, status)
		intent.putExtra(EXTRA_DONE, done)
		intent.putExtra(EXTRA_FAILED, failed)
		sendBroadcast(intent)
	}

	private fun initializePython() {
		if (!Python.isStarted()) {
			Python.start(AndroidPlatform(this))
		}
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
			channel.setShowBadge(false)
			manager.createNotificationChannel(channel)
		}
	}

	private fun buildIndeterminateNotification(content: String): Notification {
		val builder = baseBuilder(content)
		builder.setProgress(0, 0, true)
		return builder.build()
	}

	private fun buildProgressNotification(progress: Int, max: Int, content: String): Notification {
		val builder = baseBuilder(content)
		builder.setProgress(max, progress, false)
		return builder.build()
	}

	private fun buildCompletedNotification(): Notification {
		return baseBuilder(getString(R.string.notification_completed))
			.setProgress(0, 0, false)
			.setOngoing(false)
			.build()
	}

	private fun buildFailedNotification(): Notification {
		return baseBuilder(getString(R.string.notification_failed))
			.setProgress(0, 0, false)
			.setOngoing(false)
			.build()
	}

	private fun baseBuilder(content: String): NotificationCompat.Builder {
		val openIntent = Intent(this, MainActivity::class.java)
		val pending: PendingIntent = PendingIntent.getActivity(
			this,
			0,
			openIntent,
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
		)
		return NotificationCompat.Builder(this, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_download)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(content)
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.setCategory(NotificationCompat.CATEGORY_PROGRESS)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setContentIntent(pending)
	}

	private fun updateNotification(notification: Notification) {
		val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		manager.notify(FOREGROUND_ID, notification)
	}
}


