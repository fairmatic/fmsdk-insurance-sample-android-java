package com.fairmatic.fairmaticsamplejava;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.classes.AccidentInfo;
import com.fairmatic.sdk.classes.AnalyzedDriveInfo;
import com.fairmatic.sdk.classes.DriveResumeInfo;
import com.fairmatic.sdk.classes.DriveStartInfo;
import com.fairmatic.sdk.classes.EstimatedDriveInfo;
import com.fairmatic.sdk.classes.FairmaticBroadcastReceiver;

public class MyFairmaticBroadcastReceiver extends FairmaticBroadcastReceiver {


    @Override
    public void onAccident(@NonNull Context context, @Nullable AccidentInfo accidentInfo) {

    }

    @Override
    public void onDriveAnalyzed(@NonNull Context context, @Nullable AnalyzedDriveInfo analyzedDriveInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveAnalyzed");
    }

    @Override
    public void onDriveEnd(@NonNull Context context, @Nullable EstimatedDriveInfo estimatedDriveInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveEnd");
    }

    @Override
    public void onDriveResume(@NonNull Context context, @Nullable DriveResumeInfo driveResumeInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveResume");
    }

    @Override
    public void onDriveStart(@NonNull Context context, @Nullable DriveStartInfo driveStartInfo) {
        Log.d(Constants.LOG_TAG_DEBUG, "onDriveStart");
    }

    @Override
    public void onFairmaticSettingsConfigChanged(@NonNull Context context, boolean errorsFound, boolean warningsFound) {
        Log.d(Constants.LOG_TAG_DEBUG, "onFairmaticSettingsConfigChanged");

        // Persist whether the Fairmatic SDK has detected errors or warnings.
        // Use these persisted flags as a basis to determine whether Fairmatic settings
        // should be fetched on app resume.
        SharedPrefsManager prefsManager = SharedPrefsManager.sharedInstance(context);
        prefsManager.setSettingsErrorsFound(errorsFound);
        prefsManager.setSettingsWarningsFound(warningsFound);
        FairmaticManager.sharedInstance().checkFairmaticSettings(context);

    }
}
