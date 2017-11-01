package com.app.ats.com.ilocate.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager.LayoutParams

import com.app.ats.com.ilocate.R


class LockscreenUtils {

    // Member variables
    private var mOverlayDialog: OverlayDialog? = null
    private var mLockStatusChangedListener: OnLockStatusChangedListener? = null

    // Interface to communicate with owner activity
    interface OnLockStatusChangedListener {
        fun onLockStatusChanged(isLocked: Boolean)
    }

    // Reset the variables
    init {
        reset()
    }

    // Display overlay dialog with a view to prevent home button click
    fun lock(activity: Activity) {
        if (mOverlayDialog == null) {
            mOverlayDialog = OverlayDialog(activity)
try {
    mOverlayDialog!!.show()
    mLockStatusChangedListener = activity as OnLockStatusChangedListener
}catch (e:Exception){

}
        }
    }

    // Reset variables
    fun reset() {
        if (mOverlayDialog != null) {
            mOverlayDialog!!.dismiss()
            mOverlayDialog = null
        }
    }

    // Unlock the home button and give callback to unlock the screen
    fun unlock() {
        if (mOverlayDialog != null) {
            mOverlayDialog!!.dismiss()
            mOverlayDialog = null
            if (mLockStatusChangedListener != null) {
                mLockStatusChangedListener!!.onLockStatusChanged(false)
            }
        }
    }

    // Create overlay dialog for lockedscreen to disable hardware buttons
    private class OverlayDialog(activity: Activity) : AlertDialog(activity, R.style.OverlayDialog) {

        init {
            val params = window!!.attributes
            params.type = LayoutParams.TYPE_SYSTEM_ERROR
            params.dimAmount = 0.0f
            params.width = 0
            params.height = 0
            params.gravity = Gravity.BOTTOM
            window!!.attributes = params
            window!!.setFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED or LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff)
            ownerActivity = activity
            setCancelable(false)
        }

        // consume touch events
        override fun dispatchTouchEvent(motionevent: MotionEvent): Boolean {
            return true
        }

    }
}
