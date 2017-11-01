package com.app.ats.com.ilocate

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.app.ats.com.ilocate.utils.LockscreenService
import com.app.ats.com.ilocate.utils.LockscreenUtils

class RecoveryPage : Activity(), LockscreenUtils.OnLockStatusChangedListener {


    // Member variables
    private var mLockscreenUtils: LockscreenUtils? = null
    private var mPinLockView: PinLockView? = null
    private var mIndicatorDots: IndicatorDots? = null
    private var mediaPlayer: MediaPlayer? = null


    // Set appropriate flags to make the screen appear over the keyguard
    //    @Override
    //    public void onAttachedToWindow() {
    //        this.getWindow().setType(
    //                WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    //        this.getWindow().addFlags(
    //                WindowManager.LayoutParams.FLAG_FULLSCREEN
    //                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
    //                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    //                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    //        );
    //
    //        super.onAttachedToWindow();
    //    }


    override fun onPause() {
        super.onPause()
        appStatus = false
        mediaPlayer!!.stop()


    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)


        getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("page", "recovery").apply()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        mediaPlayer = MediaPlayer.create(this, R.raw.siren)
        mPinLockView = findViewById(R.id.pin_lock_view) as PinLockView
        mediaPlayer!!.isLooping = true

        mediaPlayer!!.start()
        appStatus = true
        mIndicatorDots = findViewById(R.id.indicator_dots) as IndicatorDots
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)

        mPinLockView!!.setPinLockListener(object : PinLockListener {
            var loc: Location? = null
            var locationManager: LocationManager? = null

            override fun onComplete(pin: String) {

                val pinss = getSharedPreferences("ilocate", Context.MODE_PRIVATE).getString("recovery", "0")

                if (pin == pinss) {
                    mediaPlayer!!.stop()
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("page", "start").apply()
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("pin", pinss).apply()
                    unlockHomeButton()

                } else {

                    val i = Intent(applicationContext, RecoveryPage::class.java)
                    finish()
                    startActivity(i)

                }
            }

            override fun onEmpty() {


            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {

            }
        })

        init()



            try {
                // disable keyguard
                disableKeyguard()

                // lock home button
                lockHomeButton()

                // start service for observing intents
                startService(Intent(this, LockscreenService::class.java))



            } catch (e: Exception) {
            }


    }

    private fun init() {
        mLockscreenUtils = LockscreenUtils()
    }



    override fun onResume() {
        super.onResume()
        mediaPlayer!!.start()
    }

    // Don't finish Activity on Back press
    override fun onBackPressed() {

    }

    // Handle button clicks
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {


    return true

    }

    // handle the key press events here itself
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
       return true
    }

    // Lock home button
    fun lockHomeButton() {
        mLockscreenUtils!!.lock(this@RecoveryPage)
    }

    // Unlock home button and wait for its callback
    fun unlockHomeButton() {
        mLockscreenUtils!!.unlock()
    }

    // Simply unlock device when home button is successfully unlocked
    override fun onLockStatusChanged(isLocked: Boolean) {
        if (!isLocked) {
            unlockDevice()
        }
    }

    override fun onStop() {
        super.onStop()
//        unlockHomeButton()
    }

    private fun disableKeyguard() {
        val mKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val mKL = mKM.newKeyguardLock("IN")
        mKL.disableKeyguard()
    }

    private fun enableKeyguard() {
        val mKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val mKL = mKM.newKeyguardLock("IN")
        mKL.reenableKeyguard()
    }

    //Simply unlock device by finishing the activity
    private fun unlockDevice() {
        finish()
    }

    companion object {
        var appStatus: Boolean = false
    }
}

