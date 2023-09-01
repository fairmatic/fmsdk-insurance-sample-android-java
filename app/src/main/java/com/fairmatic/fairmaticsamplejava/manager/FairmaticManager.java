package com.fairmatic.fairmaticsamplejava.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.MyFairmaticBroadcastReceiver;
import com.fairmatic.fairmaticsamplejava.MyFairmaticNotificationProvider;
import com.fairmatic.fairmaticsamplejava.utils.NotificationUtility;
import com.fairmatic.sdk.Fairmatic;
import com.fairmatic.sdk.classes.FairmaticConfiguration;
import com.fairmatic.sdk.classes.FairmaticDriverAttributes;
import com.fairmatic.sdk.classes.FairmaticIssueType;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;
import com.fairmatic.sdk.classes.FairmaticSettingError;
import com.fairmatic.sdk.classes.FairmaticSettingWarning;
import com.fairmatic.sdk.classes.FairmaticSettings;
import com.fairmatic.sdk.classes.FairmaticSettingsCallback;
import com.fairmatic.sdk.classes.GooglePlaySettingsError;

public class FairmaticManager {
    private class InsuranceInfo {
        int insurancePeriod;
        String trackingId;

        InsuranceInfo(int insurancePeriod, String trackingId) {
            this.insurancePeriod = insurancePeriod;
            this.trackingId = trackingId;
        }
    }

    // TODO - remove this before submit.
    private static final String FAIRMATIC_SDK_KEY = "UXBDuLRFg6k2YT3oys2T9njD8BEzAoA1";   // Your Fairmatic SDK Key

    private static FairmaticManager sharedInstance = new FairmaticManager();

    public static synchronized FairmaticManager  sharedInstance() {
        return sharedInstance;
    }

    private final FairmaticDriverAttributes fairmaticDriverAttributes = new FairmaticDriverAttributes(
           "John Doe",
           "john_doe@company.com",
            "1234567890"
    );

    public void initializeFairmaticSDK(final Context context, String driverId, FairmaticOperationCallback callback) {

        Log.d(Constants.LOG_TAG_DEBUG, "initializeFairmaticSDK called");
        final FairmaticConfiguration fairmaticConfiguration = new FairmaticConfiguration(
                FAIRMATIC_SDK_KEY, driverId, fairmaticDriverAttributes);
        Log.d(Constants.LOG_TAG_DEBUG, "initializeFairmaticSDK called with driverId: " + driverId);

        Fairmatic.INSTANCE.setup(
                context,
                fairmaticConfiguration,
                MyFairmaticBroadcastReceiver.class,
                MyFairmaticNotificationProvider.class,
                new FairmaticOperationCallback() {
                    @Override
                    public void onCompletion(FairmaticOperationResult result) {
                        if (result instanceof FairmaticOperationResult.Success) {
                            Log.d(Constants.LOG_TAG_DEBUG, "FairmaticSDK setup success");
                            // Update periods
                            updateFairmaticInsurancePeriod(context);
                            // Hide error if visible
                            NotificationUtility.hideFairmaticSetupFailureNotification(context);
                            SharedPrefsManager.sharedInstance(context).setRetryFairmaticSetup(false);
                        } else if (result instanceof FairmaticOperationResult.Failure) {
                            Log.d(
                                    Constants.LOG_TAG_DEBUG,
                                    String.format(
                                            "FairmaticSDK setup failed %s",
                                            ((FairmaticOperationResult.Failure) result).getError().name()
                                    )
                            );
                            // Display error
                            NotificationUtility.displayFairmaticSetupFailureNotification(context);
                            SharedPrefsManager.sharedInstance(context).setRetryFairmaticSetup(true);
                        }
                        callback.onCompletion(result);
                    }
                }
        );
    }

    public void maybeCheckFairmaticSettings(Context context) {
        SharedPrefsManager prefsManager = SharedPrefsManager.sharedInstance(context);
        if (prefsManager.isSettingsErrorFound() || prefsManager.isSettingsWarningsFound()) {
            checkFairmaticSettings(context);
        }
    }

    public void checkFairmaticSettings(final Context context) {
        // clear all previous setting error notifications.
        NotificationUtility.clearAllErrorNotifications(context);

        Fairmatic.INSTANCE.getFairmaticSettings(context, new FairmaticSettingsCallback() {
            @Override
            public void onComplete(@Nullable FairmaticSettings fairmaticSettings) {
                if (fairmaticSettings == null) {
                    // The callback returns null if the SDK is not setup.
                    return;
                }

                // Handle errors
                for (FairmaticSettingError error : fairmaticSettings.getErrors()) {
                    switch (error.getType()) {
                        case POWER_SAVER_MODE_ENABLED: {
                            Notification notification =
                                    NotificationUtility.
                                            getPSMNotification(context, true);
                            getNotificationManager(context).notify(
                                    NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                    notification);
                            break;
                        }
                        case BACKGROUND_RESTRICTION_ENABLED: {
                            Notification notification =
                                    NotificationUtility.getBackgroundRestrictedNotification(context);
                            getNotificationManager(context).notify(
                                    NotificationUtility.BACKGROUND_RESTRICTED_NOTIFICATION_ID,
                                    notification);
                            break;
                        }
                        case GOOGLE_PLAY_SETTINGS_ERROR: {
                            GooglePlaySettingsError e = (GooglePlaySettingsError) error;
                            Notification notification =
                                    NotificationUtility.
                                            getGooglePlaySettingsNotification(context,
                                                    e.getGooglePlaySettingsResult());
                            getNotificationManager(context).notify(
                                    NotificationUtility.GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID,
                                    notification);

                            break;
                        }
                        case LOCATION_PERMISSION_DENIED: {
                            Notification notification =
                                    NotificationUtility.
                                            getLocationPermissionDeniedNotification(context);
                            getNotificationManager(context).notify(
                                    NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
                                    notification);
                            break;
                        }
                    }
                }

                // Handle warnings
                for (FairmaticSettingWarning warning : fairmaticSettings.getWarnings()) {
                    if (warning.getType() == FairmaticIssueType.POWER_SAVER_MODE_ENABLED) {
                        Notification notification =
                                NotificationUtility.
                                        getPSMNotification(context, false);
                        getNotificationManager(context).notify(
                                NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
                                notification);
                    }
                }

            }
        });
    }

    public void handleInsurancePeriod1(Context context, FairmaticOperationCallback callback ){
        if (context != null) {
            Log.d(Constants.LOG_TAG_DEBUG, "handleInsurancePeriod1 called");
            Fairmatic.INSTANCE.startDriveWithPeriod1(context, result -> {
                if(callback != null)
                    callback.onCompletion(result);
            });
        }
    }

    public void handleInsurancePeriod2(Context context, FairmaticOperationCallback callback ){
        if (context != null) {
            Log.d(Constants.LOG_TAG_DEBUG, "handleInsurancePeriod2 called");
            String trackingId = ((Long)System.currentTimeMillis()).toString();
            Fairmatic.INSTANCE.startDriveWithPeriod2(context, trackingId, result -> {
                if(callback != null)
                    callback.onCompletion(result);
            });
        }
    }

    public void handleInsurancePeriod3(Context context, FairmaticOperationCallback callback ){
        if (context != null) {
            Log.d(Constants.LOG_TAG_DEBUG, "handleInsurancePeriod3 called");
            String trackingId = ((Long)System.currentTimeMillis()).toString();
            Fairmatic.INSTANCE.startDriveWithPeriod3(context, trackingId, result -> {
                if(callback != null)
                    callback.onCompletion(result);
            });
        }
    }

    public void handleStopPeriod(Context context, FairmaticOperationCallback callback ){
        if (context != null) {
            Log.d(Constants.LOG_TAG_DEBUG, "handleStopPeriod called");
            Fairmatic.INSTANCE.stopPeriod(context, result -> {
                if(callback != null)
                    callback.onCompletion(result);
            });
        }
    }

    public void updateFairmaticInsurancePeriod(Context context) {
        Log.d("check123", "updateFairmaticInsurancePeriod called");
        InsuranceInfo insuranceInfo = currentlyActiveInsurancePeriod(context);
        if (insuranceInfo == null) {
            Log.d(Constants.LOG_TAG_DEBUG, "updateFairmaticInsurancePeriod with NO period");
            Fairmatic.INSTANCE.stopPeriod(context, new FairmaticOperationCallback() {
                @Override
                public void onCompletion(@NonNull FairmaticOperationResult fairmaticOperationResult) {
                    Log.d(Constants.LOG_TAG_DEBUG, "stopPeriod called");
                }
            });
        } else if (insuranceInfo.insurancePeriod == 3) {
            Log.d(Constants.LOG_TAG_DEBUG,
                    String.format("updateFairmaticInsurancePeriod with period %d and id: %s",
                            insuranceInfo.insurancePeriod,
                            insuranceInfo.trackingId));
            Fairmatic.INSTANCE.startDriveWithPeriod3(context, insuranceInfo.trackingId,
                    new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(@NonNull FairmaticOperationResult fairmaticOperationResult) {
                            Log.d(Constants.LOG_TAG_DEBUG, "startDriveWithPeriod3 called");
                        }
                    });
        } else if (insuranceInfo.insurancePeriod == 2) {
            Log.d(Constants.LOG_TAG_DEBUG,
                    String.format("updateFairmaticInsurancePeriod with period %d and id: %s",
                            insuranceInfo.insurancePeriod,
                            insuranceInfo.trackingId));
            Fairmatic.INSTANCE.startDriveWithPeriod2(context, insuranceInfo.trackingId,
                    new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(@NonNull FairmaticOperationResult fairmaticOperationResult) {
                            Log.d(Constants.LOG_TAG_DEBUG, "startDriveWithPeriod2 called");
                        }
                    });
        } else {
            Log.d(Constants.LOG_TAG_DEBUG,
                    String.format("updateFairmaticInsurancePeriod with period %d",
                            insuranceInfo.insurancePeriod));
            Fairmatic.INSTANCE.startDriveWithPeriod1(context, new FairmaticOperationCallback() {
                @Override
                public void onCompletion(@NonNull FairmaticOperationResult fairmaticOperationResult) {
                    Log.d(Constants.LOG_TAG_DEBUG, "startDriveWithPeriod1 called");
                }
            });
        }
    }

    private InsuranceInfo currentlyActiveInsurancePeriod(Context context) {
        TripManager.State state = TripManager.sharedInstance(context).getTripManagerState();
        if (!state.isUserOnDuty()) {
            return null;
        } else if (state.getPassengersInCar()) {
            return new InsuranceInfo(3, state.getTrackingId());
        } else if (state.getPassengersWaitingForPickup()) {
            return new InsuranceInfo(2, state.getTrackingId());
        } else {
            return new InsuranceInfo(1, null);
        }
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
    }
}
