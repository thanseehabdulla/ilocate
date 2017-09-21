package com.app.ats.com.ilocate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

/**
 * Created by abdulla on 1/6/17.
 */

public class StartActivity extends Activity {

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private EditText ed;
    Button b2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("page", "start").apply();

        b2 = (Button) findViewById(R.id.button2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), profileact.class);
                startActivity(i);
                finish();

            }
        });

        ed = (EditText) findViewById(R.id.editText);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);

        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {

                String email = ed.getText().toString();
                if (email.equals(""))
                    Toast.makeText(getApplicationContext(), "please fill all the forms", Toast.LENGTH_SHORT).show();
                else {
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("pin", pin).apply();
                    getSharedPreferences("e1", MODE_PRIVATE).edit().putString("e3", email).apply();
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putInt("count", 0).apply();
                    Toast.makeText(getApplicationContext(), "Changes have been saved , App is minimizing for protection service", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onEmpty() {


            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });


    }
}
