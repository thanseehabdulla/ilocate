package com.app.ats.com.ilocate;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.app.ats.com.ilocate.gmailpackage.GMailSender;
import com.app.ats.com.ilocate.photohandler.PhotoHandler;
import com.app.ats.com.ilocate.utils.LockscreenService;
import com.app.ats.com.ilocate.utils.LockscreenUtils;

import java.security.SecureRandom;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.R.id.message;

public class MainActivity extends Activity implements
        LockscreenUtils.OnLockStatusChangedListener {


    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;



    // Member variables
    private LockscreenUtils mLockscreenUtils;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private String phoneNo;
    private String messages;
    public static boolean appStatus;
    private Camera camera;
    private int cameraId = 0;
    public Context context;

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
//        try {
//            camera.release();
//            camera = null;
//        }catch (Exception e) {
//        }

        }

    protected void sendSMSMessage(String pin, String mobile, GPSTracker loc) {
        phoneNo = mobile;


        // check if GPS enabled
        if(loc.canGetLocation()){

            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            loc.showSettingsAlert();
        }



try {
    messages = "The recovery pin is :" + pin + "from  latitude" + loc.getLatitude() + "longitude" + loc.getLongitude();
}catch (Exception e){
    messages = "The recovery pin is :" + pin ;
}
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, messages, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                               Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);

        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        appStatus = true;

        mPinLockView.setPinLockListener(new PinLockListener() {
            public int count;
            public GPSTracker loc;
            public LocationManager locationManager;

            @Override
            public void onComplete(String pin) {

                String pinss = getSharedPreferences("ilocate", MODE_PRIVATE).getString("pin", "0");
                String mobile = getSharedPreferences("ilocate", MODE_PRIVATE).getString("email", "0");
                 count = getSharedPreferences("ilocate", MODE_PRIVATE).getInt("count", 0);
                if (pin.equals(pinss)) {
                    unlockHomeButton();
                    getSharedPreferences("ilocate", MODE_PRIVATE).edit().putInt("count", 0).apply();
                }
                else if (count <= 5) {
                    Toast.makeText(getApplicationContext(), "Please try Again, You have " + (5 - count) + " chances left", Toast.LENGTH_SHORT).show();
                    count++;
                    getSharedPreferences("ilocate",MODE_PRIVATE).edit().putInt("count",count).apply();
                } else if (count > 5) {

                    loc = new GPSTracker(MainActivity.this);


                    SecureRandom random = new SecureRandom();
                    int num = random.nextInt(10000);
                    String formatted = String.format("%04d", num);

                    // check if GPS enabled
                    if(loc.canGetLocation()){

                        double latitude = loc.getLatitude();
                        double longitude = loc.getLongitude();

                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                                + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    }else{
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        loc.showSettingsAlert();
                    }



                    try {
                        messages = "The recovery pin is :" + formatted + "from  latitude" + loc.getLatitude() + "longitude" + loc.getLongitude();
                    }catch (Exception e){
                        messages = "The recovery pin is :" + formatted ;
                    }


//                    try {
//                        GMailSender sender = new GMailSender("thanseehabdulla@gmail.com", "thanseeh");
//                        sender.sendMail("Ilocate",
//                                messages,
//                                "thanseehabdulla@gmail.com",
//                                "thanseehabdulla@gmail.com");
//                    } catch (Exception e) {
//                        Log.e("SendMail", e.getMessage(), e);
//                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
//                    }





                    if (!getPackageManager()
                            .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        Toast.makeText(getApplicationContext(), "No camera on this device", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        cameraId = findFrontFacingCamera();
                        if (cameraId < 0) {
                            Toast.makeText(getApplicationContext(), "No front facing camera found.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                camera = Camera.open(cameraId);
                            }catch (Exception e){

                            }
                                camera.startPreview();
                            camera.takePicture(null, null,
                                    new PhotoHandler(getApplicationContext()));
                        }

                    }





                    new GmailAsync(formatted,mobile,messages,getApplicationContext()).execute();

//                    sendSMSMessage(formatted,mobile,loc);
                    getSharedPreferences("ilocate",MODE_PRIVATE).edit().putString("recovery",formatted).apply();


                }

            }

                private int findFrontFacingCamera() {
                    int cameraId = -1;
                    // Search for the front facing camera
                    int numberOfCameras = Camera.getNumberOfCameras();
                    for (int i = 0; i < numberOfCameras; i++) {
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        Camera.getCameraInfo(i, info);
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                            Log.d(DEBUG_TAG, "Camera found");
                            cameraId = i;
                            break;
                        }
                    }
                    return cameraId;
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
    };

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            return true;
        }

        return false;

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(MainActivity.this);
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
    private void unlockDevice()
    {
        finish();
    }


    class GmailAsync extends AsyncTask<Void, Void, Void> {

        private Exception exception;
        private String formatted,mobile;
        private String messages;
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);;
        private Context context;

        public GmailAsync(String formatted, String mobile, String loc, Context context) {
            this.formatted=formatted;
            this.mobile=mobile;
            this.messages=loc;
            this.context=context;


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("infinitmail006@gmail.com", "namoideen");
                        }
                    });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("infinitmail006@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(mobile));
                message.setSubject("ILocate");


                        MimeBodyPart messageBodyPart = new MimeBodyPart();

                        Multipart multipart = new MimeMultipart();

                        messageBodyPart = new MimeBodyPart();
                        String fileName = "images";
                        String file =  context.getSharedPreferences("ilocate", MODE_PRIVATE).getString("image","0");
                        DataSource source = new FileDataSource(file);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(fileName);
                        multipart.addBodyPart(messageBodyPart);



                BodyPart messageBodyPart2 = new MimeBodyPart();
                messageBodyPart2.setText("Dear user,"
                        + "\n\n " + messages);
                multipart.addBodyPart(messageBodyPart2);


                message.setContent(multipart);
                Transport.send(message);

//                        System.out.println("Done");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }


            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        dialog.dismiss();

            Intent iss = new Intent(context,RecoveryPage.class);
            finish();
            startActivity(iss);



        }


    }


}
