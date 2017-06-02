package com.app.ats.com.ilocate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.ats.com.ilocate.MainActivity;

public class LockscreenIntentReceiver extends BroadcastReceiver {

	// Handle actions and display Lockscreen
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)
				|| intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            start_lockscreen(context);
		}

	}

	// Display lock screen
	private void start_lockscreen(Context context) {
		Intent mIntent = new Intent(context, MainActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(mIntent);
	}

}
