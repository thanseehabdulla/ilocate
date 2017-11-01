package com.app.ats.com.ilocate

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.app.ats.com.ilocate.photohandler.PhotoHandler
import com.app.ats.com.ilocate.utils.LockscreenService
import com.app.ats.com.ilocate.utils.LockscreenUtils
import java.security.SecureRandom
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MainActivity : Activity(), LockscreenUtils.OnLockStatusChangedListener {


    // Member variables
    private var mLockscreenUtils: LockscreenUtils? = null
    private var mPinLockView: PinLockView? = null
    private var mIndicatorDots: IndicatorDots? = null
    private var phoneNo: String? = null
    private var messages: String? = null
    private var camera: Camera? = null
    private var cameraId = 0
    var context: Context? = null



    override fun onPause() {
        super.onPause()
        appStatus = false
                try {
                    camera!!.release();
                    camera = null;
                }catch (e: Exception) {
                }

    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.activity_main)


        mPinLockView = findViewById(R.id.pin_lock_view) as PinLockView

        mIndicatorDots = findViewById(R.id.indicator_dots) as IndicatorDots
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)

        appStatus = true


        mPinLockView!!.setPinLockListener(object : PinLockListener {
            var count: Int = 0
            lateinit var loc: GPSTracker
            var locationManager: LocationManager? = null


            @SuppressLint("MissingPermission")
            override fun onComplete(pin: String) {

                val pinss = getSharedPreferences("ilocate", Context.MODE_PRIVATE).getString("pin", "0")
                val mobile = getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e3", "0")
                count = getSharedPreferences("ilocate", Context.MODE_PRIVATE).getInt("count", 0)
                if (pin == pinss) {
                    unlockHomeButton()
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putInt("count", 0).apply()
                } else if (count < 1) {
                    Toast.makeText(applicationContext, "Please try Again, You have " + (5 - count) + " chances left", Toast.LENGTH_SHORT).show()
                    count++
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putInt("count", count).apply()
                    val i = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    startActivity(i)
                } else if (count >= 1) {

                    loc = GPSTracker(this@MainActivity)


                    val random = SecureRandom()
                    val num = random.nextInt(10000)
                    val formatted = String.format("%04d", num)

                    // check if GPS enabled
                    if (loc.canGetLocation()) {

                        val latitude = loc.getLatitude()
                        val longitude = loc.getLongitude()

                        // \n is for new line
                        Toast.makeText(applicationContext, "Your Location is - \nLat: "
                                + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show()
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        loc.showSettingsAlert()
                    }


                    try {
//                        val telemamanger = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                        val getSimSerialNumber = telemamanger.simSerialNumber
//                        val getSimNumber = telemamanger.line1Number

                        val s: String
//                        if (getSimNumber == "")
                            s = " not allowed by carrier"
//                        else
//                            s = getSimNumber
//                        try {
                            messages = "The recovery pin is :" + formatted + "from  latitude" + loc.getLatitude() + "longitude" + loc.getLongitude() + "current phone number used" + s
//                        } catch (e: Exception) {
//                            messages = "The recovery pin is :" + formatted
//                        }




                        if (!packageManager
                                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            Toast.makeText(applicationContext, "No camera on this device", Toast.LENGTH_LONG)
                                    .show()
                        } else {
                            cameraId = findFrontFacingCamera()
                            if (cameraId < 0) {
                                Toast.makeText(applicationContext, "No front facing camera found.",
                                        Toast.LENGTH_LONG).show()
                            } else {
                                try {
                                    camera = Camera.open(cameraId)
                                } catch (e: Exception) {

                                }

                                camera!!.startPreview()
                                camera!!.takePicture(null, null,
                                        PhotoHandler(applicationContext))
                            }

                        }


                        messages?.let { GmailAsync(formatted, mobile, it, applicationContext).execute() }

                        //                    sendSMSMessage(formatted,mobile,loc);
                        getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("recovery", formatted).apply()
                    } catch (e: Exception) {
//                        sendSMSMessage(formatted, getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e2", "0"), loc)
                    }

                }

            }

            private fun findFrontFacingCamera(): Int {
                var cameraId = -1
                // Search for the front facing camera
                val numberOfCameras = Camera.getNumberOfCameras()
                for (i in 0 until numberOfCameras) {
                    val info = Camera.CameraInfo()
                    Camera.getCameraInfo(i, info)
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        //                            Log.d(DEBUG_TAG, "Camera found");
                        cameraId = i
                        break
                    }
                }
                return cameraId
            }

            override fun onEmpty() {


            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {

            }
        })

        init()

        // unlock screen in case of app get killed by system
//        if (intent != null && intent.hasExtra("kill")
//                && intent.extras!!.getInt("kill") == 1) {
//            enableKeyguard()
//            unlockHomeButton()
//        } else {

            try {
                // disable keyguard
                disableKeyguard()

                // lock home button
                lockHomeButton()

                // start service for observing intents
                startService(Intent(this, LockscreenService::class.java))

                // listen the events get fired during the call
//                val phoneStateListener = StateListener()
//                val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                telephonyManager.listen(phoneStateListener,
//                        PhoneStateListener.LISTEN_CALL_STATE)

            } catch (e: Exception) {
            }

//        }
    }

    private fun init() {
        mLockscreenUtils = LockscreenUtils()
    }



    // Don't finish Activity on Back press
    override fun onBackPressed() {

    }



    // Handle button clicks
    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent): Boolean {
        return true;
    }

    // handle the key press events here itself
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
     return true;
    }

    // Lock home button
    fun lockHomeButton() {
        mLockscreenUtils!!.lock(this@MainActivity)
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


    internal inner class GmailAsync(private val formatted: String, private val mobile: String, private val messages: String, private val context: Context) : AsyncTask<Void, Void, Boolean>() {

        private val exception: Exception? = null
        private val dialog = ProgressDialog(this@MainActivity)


        override fun onPreExecute() {
            super.onPreExecute()
            dialog.setCancelable(false)
            dialog.show()
        }

        override fun doInBackground(vararg params: Void): Boolean? {

            val props = Properties()
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.port", "587")

            val session = Session.getInstance(props,
                    object : javax.mail.Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication? {
                            return PasswordAuthentication("infinitmail006@gmail.com", "namoideen")
                        }
                    })
            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("infinitmail006@gmail.com"))
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(mobile))
                message.subject = "ILocate"


                var messageBodyPart = MimeBodyPart()

                val multipart = MimeMultipart()

                messageBodyPart = MimeBodyPart()
                val fileName = "images"
                val file = context.getSharedPreferences("ilocate", Context.MODE_PRIVATE).getString("image", "0")
                val source = FileDataSource(file)
                messageBodyPart.dataHandler = DataHandler(source)
                messageBodyPart.fileName = fileName
                multipart.addBodyPart(messageBodyPart)


                val messageBodyPart2 = MimeBodyPart()
                messageBodyPart2.setText("Dear user,"
                        + "\n\n " + messages)
                multipart.addBodyPart(messageBodyPart2)


                message.setContent(multipart)
                Transport.send(message)

                //                        System.out.println("Done");

            } catch (e: MessagingException) {
                throw RuntimeException(e)
            }


            return true
        }

        override fun onPostExecute(feed: Boolean) {
            // TODO: check this.exception
            // TODO: do something with the feed
            dialog.dismiss()

            val iss = Intent(context, RecoveryPage::class.java)
            finish()
            startActivity(iss)


        }


    }

    companion object {


        val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10


        val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()

        private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
        public var appStatus: Boolean = false
    }


}
