package com.app.ats.com.ilocate.utils

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v7.app.NotificationCompat

import com.app.ats.com.ilocate.R

/**
 * Created by abdulla on 1/11/17.
 */

class LockscreenService : Service() {

    private var mReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    // Register for Lockscreen event intents
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        		filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = LockscreenIntentReceiver()
        registerReceiver(mReceiver, filter)
        startForeground()
        return Service.START_STICKY
    }

    // Run service in foreground so it is less likely to be killed by system
    private fun startForeground() {
        val notification = NotificationCompat.Builder(this)
                .setContentTitle(resources.getString(R.string.app_name))
                .setTicker(resources.getString(R.string.app_name))
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build()
        startForeground(9999, notification)
    }

    // Unregister receiver
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
}
