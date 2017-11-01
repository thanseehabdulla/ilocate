package com.app.ats.com.ilocate.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent

import com.app.ats.com.ilocate.MainActivity
import com.app.ats.com.ilocate.RecoveryPage

/**
 * Created by abdulla on 1/11/17.
 */

class LockscreenIntentReceiver : BroadcastReceiver() {

    // Handle actions and display Lockscreen
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_SCREEN_ON || intent.action == Intent.ACTION_BOOT_COMPLETED) {

            start_lockscreen(context)

        }

    }

    // Display lock screen
    private fun start_lockscreen(context: Context) {


        val mIntent: Intent

        val value = context.getSharedPreferences("ilocate", MODE_PRIVATE).getString("page", "0")
        if (value == "start") {
            if (MainActivity.appStatus == false) {
                mIntent = Intent(context, MainActivity::class.java)
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                context.startActivity(mIntent)
            }
        } else {
            if (RecoveryPage.appStatus == false) {
                mIntent = Intent(context, RecoveryPage::class.java)

                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                context.startActivity(mIntent)
            }
        }
    }
}
