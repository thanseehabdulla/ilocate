package com.app.ats.com.ilocate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

/**
 * Created by abdulla on 1/6/17.
 */

public class StartActivity extends Activity{

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private EditText ed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("page", "start").apply();



       ed = (EditText) findViewById(R.id.editText);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);

        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {

                String email = ed.getText().toString();
                if(email.equals(""))
                    Toast.makeText(getApplicationContext(),"please fill all the forms",Toast.LENGTH_SHORT).show();
               else {
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("pin", pin).apply();
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("email", email).apply();
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
