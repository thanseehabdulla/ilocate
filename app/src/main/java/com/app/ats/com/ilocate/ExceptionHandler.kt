package com.app.ats.com.ilocate

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Process

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by abdulla on 17/3/17.
 */

class ExceptionHandler(private val myContext: Activity) : Thread.UncaughtExceptionHandler {
    private val LINE_SEPARATOR = "\n"

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        val errorReport = "************ CAUSE OF ERROR ************\n\n" +
                stackTrace.toString() +
                "\n************ DEVICE INFORMATION ***********\n" +
                "Brand: " +
                Build.BRAND +
                LINE_SEPARATOR +
                "Device: " +
                Build.DEVICE +
                LINE_SEPARATOR +
                "Model: " +
                Build.MODEL +
                LINE_SEPARATOR +
                "Id: " +
                Build.ID +
                LINE_SEPARATOR +
                "Product: " +
                Build.PRODUCT +
                LINE_SEPARATOR +
                "\n************ FIRMWARE ************\n" +
                "SDK: " +
                Build.VERSION.SDK +
                LINE_SEPARATOR +
                "Release: " +
                Build.VERSION.RELEASE +
                LINE_SEPARATOR +
                "Incremental: " +
                Build.VERSION.INCREMENTAL +
                LINE_SEPARATOR

        val intent = Intent(myContext, MainActivity::class.java)
        intent.putExtra("error", errorReport)
        myContext.startActivity(intent)

        Process.killProcess(Process.myPid())
        System.exit(10)
    }


}
