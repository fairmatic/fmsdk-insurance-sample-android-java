package com.fairmatic.fairmaticsamplejava;

import android.app.Application;
import android.widget.Toast;

import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

public class SampleAppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String driverId = SharedPrefsManager.sharedInstance(this).getDriverId();
        // if the user is already logged in or setup previously failed, Fairmatic SDK should be set up.
        if (driverId != null ||
                SharedPrefsManager.sharedInstance(this).shouldRetryFairmaticSetup()) {
            FairmaticManager.sharedInstance().initializeFairmaticSDK(this, driverId, new FairmaticOperationCallback() {
                @Override
                public void onCompletion(FairmaticOperationResult result) {
                    if (result instanceof FairmaticOperationResult.Failure) {
                        String errorMessage = String.valueOf(((FairmaticOperationResult.Failure) result).getError());
                        Toast.makeText(SampleAppApplication.this, "Failed to initialize Fairmatic SDK : " + errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SampleAppApplication.this, "Successfully initialized Fairmatic SDK", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}
