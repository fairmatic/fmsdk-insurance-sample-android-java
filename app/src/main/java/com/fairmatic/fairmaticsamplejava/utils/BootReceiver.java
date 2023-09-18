package com.fairmatic.fairmaticsamplejava.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fairmatic.fairmaticsamplejava.Constants;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is a no-op as Fairmatic SDK initialisation has already happened in the Application class.
        Log.d ( Constants.LOG_TAG_DEBUG, "BootReceiver onReceive called with intent "+  intent.getAction());

    }
}
