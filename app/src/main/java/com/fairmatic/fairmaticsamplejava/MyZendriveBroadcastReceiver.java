package com.fairmatic.fairmaticsamplejava;

import android.content.Context;
import android.util.Log;

import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.AnalyzedDriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.EstimatedDriveInfo;
import com.zendrive.sdk.ZendriveBroadcastReceiver;

public class MyZendriveBroadcastReceiver extends ZendriveBroadcastReceiver {
    @Override
    public void onDriveStart(Context context, DriveStartInfo driveStartInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveStart");
    }

    @Override
    public void onDriveEnd(Context context, EstimatedDriveInfo estimatedDriveInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveEnd");
    }

    @Override
    public void onDriveAnalyzed(Context context, AnalyzedDriveInfo analyzedDriveInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveAnalyzed");
    }

    @Override
    public void onDriveResume(Context context, DriveResumeInfo driveResumeInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveResume");
    }

    @Override
    public void onAccident(Context context, AccidentInfo accidentInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onAccident");
    }

    @Override
    public void onZendriveSettingsConfigChanged(Context context, boolean errorsFound,
                                                boolean warningsFound) {
        Log.d(Constants.LOG_TAG_DEBUG, "onZendriveSettingsConfigChanged");

        // Persist whether the Zendrive SDK has detected errors or warnings.
        // Use these persisted flags as a basis to determine whether Zendrive settings
        // should be fetched on app resume.
        SharedPrefsManager prefsManager = SharedPrefsManager.sharedInstance(context);
        prefsManager.setSettingsErrorsFound(errorsFound);
        prefsManager.setSettingsWarningsFound(warningsFound);
        //ZendriveManager.sharedInstance().checkZendriveSettings(context);
    }
}
