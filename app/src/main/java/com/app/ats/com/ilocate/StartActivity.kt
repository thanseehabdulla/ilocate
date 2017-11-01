package com.app.ats.com.ilocate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView

/**
 * Created by abdulla on 1/6/17.
 */

class StartActivity : Activity() {

    private var mPinLockView: PinLockView? = null
    private var mIndicatorDots: IndicatorDots? = null
    private var ed: EditText? = null
    internal lateinit var b2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("page", "start").apply()

        b2 = findViewById(R.id.button2) as Button

        b2.setOnClickListener {
            val i = Intent(applicationContext, profileact::class.java)
            startActivity(i)
            finish()
        }

        ed = findViewById(R.id.editText) as EditText

        mPinLockView = findViewById(R.id.pin_lock_view) as PinLockView

        mIndicatorDots = findViewById(R.id.indicator_dots) as IndicatorDots
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)

        ed!!.setText(getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e3", ""))

        mPinLockView!!.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String) {

                val email = ed!!.text.toString()
                if (email == "")
                    Toast.makeText(applicationContext, "please fill all the forms", Toast.LENGTH_SHORT).show()
                else {
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putString("pin", pin).apply()
                    getSharedPreferences("e1", Context.MODE_PRIVATE).edit().putString("e3", email).apply()
                    getSharedPreferences("ilocate", Context.MODE_PRIVATE).edit().putInt("count", 0).apply()
                    Toast.makeText(applicationContext, "Changes have been saved , App is minimizing for protection service", Toast.LENGTH_SHORT).show()

                    val i = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    startActivity(i)
                }
            }

            override fun onEmpty() {


            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {

            }
        })


    }
}
