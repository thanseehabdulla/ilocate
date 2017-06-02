package com.app.ats.com.ilocate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.ats.com.ilocate.MainActivity;
import com.app.ats.com.ilocate.RecoveryPage;

import static android.content.Context.MODE_PRIVATE;

public class LockscreenIntentReceiver extends BroadcastReceiver {

	// Handle actions and display Lockscreen
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)
				|| intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        if(MainActivity.appStatus == false)
            start_lockscreen(context);

		}

	}

	// Display lock screen
	private void start_lockscreen(Context context) {


		Intent mIntent;

		String value=context.getSharedPreferences("ilocate", MODE_PRIVATE).getString("page","0");
		if(value.equals("start"))
		mIntent= new Intent(context, MainActivity.class);
		else
			mIntent= new Intent(context, RecoveryPage.class);

		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		context.startActivity(mIntent);
	}

}
