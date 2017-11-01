package com.app.ats.com.ilocate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

/**
 * Created by abdulla on 1/8/17.
 */

class profileact : Activity() {


    internal lateinit var b2: Button
    internal lateinit var e1: EditText
    internal lateinit var e2: EditText
    internal lateinit var e3: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actprof)

        b2 = findViewById(R.id.button3) as Button
        e1 = findViewById(R.id.ed1) as EditText
        e2 = findViewById(R.id.ed2) as EditText
        e3 = findViewById(R.id.ed3) as EditText


        if (getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e1", "0") != "0")
            e1.setText(getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e1", "0"))

        if (getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e2", "0") != "0")
            e2.setText(getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e2", "0"))

        if (getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e3", "0") != "0")
            e3.setText(getSharedPreferences("e1", Context.MODE_PRIVATE).getString("e3", "0"))


        b2.setOnClickListener {
            getSharedPreferences("e1", Context.MODE_PRIVATE).edit().putString("e1", e1.text.toString()).apply()
            getSharedPreferences("e1", Context.MODE_PRIVATE).edit().putString("e2", e2.text.toString()).apply()
            getSharedPreferences("e1", Context.MODE_PRIVATE).edit().putString("e3", e3.text.toString()).apply()
            val i = Intent(applicationContext, StartActivity::class.java)
            startActivity(i)
            finish()
        }

    }
}
