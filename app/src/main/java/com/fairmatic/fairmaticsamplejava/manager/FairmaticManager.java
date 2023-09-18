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

    // TODO - Add your Fairmatic SDK Key here
    private static final String FAIRMATIC_SDK_KEY = "";   // Your Fairmatic SDK Key

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
                            // Hide error if visible
                            NotificationUtility.hideFairmaticSetupFailureNotification(context);
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
                        }
                        callback.onCompletion(result);
                    }
                }
        );
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

    public void startInsurancePeriod1(Context context, FairmaticOperationCallback callback ){
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 1 called");
        Fairmatic.INSTANCE.startDriveWithPeriod1(context, callback);
    }

    public void startInsurancePeriod2(Context context, FairmaticOperationCallback callback ){
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 2 called");
        String trackingId = "P2-"+((Long)System.currentTimeMillis()).toString();
        Fairmatic.INSTANCE.startDriveWithPeriod2(
                context,
                trackingId,
                callback);
    }

    public void startInsurancePeriod3(Context context, FairmaticOperationCallback callback ){
        Log.d(Constants.LOG_TAG_DEBUG, "Start insurance period 2 called");
        String trackingId = "P3-"+((Long)System.currentTimeMillis());
        Fairmatic.INSTANCE.startDriveWithPeriod3(
                context,
                trackingId,
                callback);
    }

    public void stopPeriod(Context context, FairmaticOperationCallback callback ){
        Log.d(Constants.LOG_TAG_DEBUG, "Stop insurance period called");
        Fairmatic.INSTANCE.stopPeriod(context, callback);
    }


    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
    }
}
