package com.app.ats.com.ilocate;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.app.ats.com.ilocate.utils.LockscreenService;
import com.app.ats.com.ilocate.utils.LockscreenUtils;

public class RecoveryPage extends Activity implements
        LockscreenUtils.OnLockStatusChangedListener {


    // Member variables
    private LockscreenUtils mLockscreenUtils;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    public static boolean appStatus;
    private MediaPlayer mediaPlayer;


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


    @Override
    protected void onPause() {
        super.onPause();
        appStatus = false;
        mediaPlayer.stop();

//        try {
//            camera.release();
//            camera = null;
//        }catch (Exception e) {
//        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("page", "recovery").apply();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mediaPlayer = MediaPlayer.create(this, R.raw.siren);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mediaPlayer.setLooping(true);

        mediaPlayer.start();
        appStatus = true;
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mPinLockView.setPinLockListener(new PinLockListener() {
            public Location loc;
            public LocationManager locationManager;

            @Override
            public void onComplete(String pin) {

                String pinss = getSharedPreferences("ilocate", MODE_PRIVATE).getString("recovery", "0");

                if (pin.equals(pinss)) {
                    mediaPlayer.stop();
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("page", "start").apply();
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putString("pin", pinss).apply();
                    unlockHomeButton();

                } else {

                    Intent i = new Intent(getApplicationContext(), RecoveryPage.class);
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

        init();

        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            try {
                // disable keyguard
                disableKeyguard();

                // lock home button
                lockHomeButton();

                // start service for observing intents
                startService(new Intent(this, LockscreenService.class));

                // listen the events get fired during the call
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {
            }

        }
    }

    private void init() {
        mLockscreenUtils = new LockscreenUtils();
    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockHomeButton();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //call whatever the function was supposed to be for DPAD_UP and consume the event
//                return false;
            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }  if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //call whatever the function was supposed to be for DPAD_DOWN and consume the event
            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }  if (keyCode == KeyEvent.KEYCODE_POWER) {
            //call whatever the function was supposed to be for DPAD_DOWN and consume the event
            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }  if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            //call whatever the function was supposed to be for DPAD_DOWN and consume the event
            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }  if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }  if ((keyCode == KeyEvent.KEYCODE_MENU)) {

            Toast.makeText(getApplicationContext(), "volume button is disabled", Toast.LENGTH_LONG).show();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("", "Dispath event power");
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            return true;
        }

            // pass the key event onto the OS
            return super.onKeyDown(keyCode, event);


    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return true;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("", "Dispath event power");
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            return true;
        }
        return false;
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(RecoveryPage.this);
    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unlockHomeButton();
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice() {
        finish();
    }
}

