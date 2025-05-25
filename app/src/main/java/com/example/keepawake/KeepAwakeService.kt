package com.example.keepawake

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class KeepAwakeService : Service() {

    companion object {
        const val ACTION_STOP = "ACTION_STOP"
        private const val CHANNEL_ID = "keep_awake_channel"
        private const val NOTIFICATION_ID = 1
        // 24h timeout as a safety net
        private const val WAKELOCK_TIMEOUT_MS = 24 * 60 * 60 * 1000L

        fun start(ctx: Context) {
            Intent(ctx, KeepAwakeService::class.java).also {
                ctx.startForegroundService(it)
            }
        }

        fun stop(ctx: Context) {
            Intent(ctx, KeepAwakeService::class.java).also {
                it.action = ACTION_STOP
                ctx.startService(it)
            }
        }
    }

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()

        // 1) Create the notification channel
        NotificationChannel(
            CHANNEL_ID,
            "Keep Awake",
            NotificationManager.IMPORTANCE_LOW
        ).also { channel ->
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        // 2) Initialize a FULL wake lock to keep both screen & CPU on
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "KeepAwake::screen"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            disable()
        } else {
            enable()
        }
        return START_STICKY
    }

    private fun enable() {
        // Acquire with timeout to guarantee eventual release
        if (!wakeLock.isHeld) wakeLock.acquire(WAKELOCK_TIMEOUT_MS)

        KeepAwakeState.setActive(this, true)
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    private fun disable() {
        if (wakeLock.isHeld) wakeLock.release()
        KeepAwakeState.setActive(this, false)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, KeepAwakeService::class.java).apply {
            action = ACTION_STOP
        }
        val pi = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Keep-Awake Mode ON")
            .setSmallIcon(R.drawable.ic_stay_awake)
            .addAction(R.drawable.ic_power_off, "Turn Off", pi)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
