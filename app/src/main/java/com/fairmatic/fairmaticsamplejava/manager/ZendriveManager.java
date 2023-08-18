//package com.fairmatic.fairmaticsamplejava.manager;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.util.Log;
//
//import com.fairmatic.fairmaticsamplejava.Constants;
//import com.fairmatic.fairmaticsamplejava.MyFairmaticBroadcastReceiver;
//import com.fairmatic.fairmaticsamplejava.MyFairmaticNotificationProvider;
//import com.fairmatic.fairmaticsamplejava.MyZendriveBroadcastReceiver;
//import com.fairmatic.fairmaticsamplejava.MyZendriveNotificationProvider;
//import com.fairmatic.fairmaticsamplejava.utils.NotificationUtility;
//import com.fairmatic.sdk.classes.FairmaticConfiguration;
//import com.fairmatic.sdk.classes.FairmaticDriveDetectionMode;
//
//public class ZendriveManager {
//
//    private class InsuranceInfo {
//        int insurancePeriod;
//        String trackingId;
//
//        InsuranceInfo(int insurancePeriod, String trackingId) {
//            this.insurancePeriod = insurancePeriod;
//            this.trackingId = trackingId;
//        }
//    }
//
//    // TODO - remove this before submit.
//    private static final String ZENDRIVE_SDK_KEY = "UXBDuLRFg6k2YT3oys2T9njD8BEzAoA1";   // Your Zendrive SDK Key
//
//    private static ZendriveManager sharedInstance = new ZendriveManager();
//
//    private fairmaticDriverAttributes = FairmaticDriverAttributes("John Doe",
//            "john_doe@company.com",
//            "1234567890"
//    )
//
//    public static synchronized ZendriveManager sharedInstance() {
//        return sharedInstance;
//    }
//
//    public void initializeZendriveSDK(final Context context, String driverId) {
//        Log.d(Constants.LOG_TAG_DEBUG, "initializeZendriveSDK called");
//        final FairmaticConfiguration zendriveConfiguration = new FairmaticConfiguration(
//                ZENDRIVE_SDK_KEY, driverId, );
//
//        Zendrive.setup(
//                context,
//                zendriveConfiguration,
//                MyZendriveBroadcastReceiver.class,
//                MyZendriveNotificationProvider.class,
//                new ZendriveOperationCallback() {
//                    @Override
//                    public void onCompletion(ZendriveOperationResult result) {
//                        if (result.isSuccess()) {
//                            Log.d(Constants.LOG_TAG_DEBUG, "ZendriveSDK setup success");
//                            // Update periods
//                            updateZendriveInsurancePeriod(context);
//                            // Hide error if visible
//                            NotificationUtility.hideZendriveSetupFailureNotification(context);
//                            SharedPrefsManager.sharedInstance(context).setRetryZendriveSetup(false);
//                        } else {
//                            Log.d(Constants.LOG_TAG_DEBUG,
//                                    String.format("ZendriveSDK setup failed %s",
//                                            result.getErrorCode().toString()));
//                            // Display error
//                            NotificationUtility.displayZendriveSetupFailureNotification(context);
//                            SharedPrefsManager.sharedInstance(context).setRetryZendriveSetup(true);
//                        }
//                    }
//                }
//        );
//    }
//
//    public void maybeCheckZendriveSettings(Context context) {
//        SharedPrefsManager prefsManager = SharedPrefsManager.sharedInstance(context);
//        if (prefsManager.isSettingsErrorFound() || prefsManager.isSettingsWarningsFound()) {
//            checkZendriveSettings(context);
//        }
//    }
//
//    public void checkZendriveSettings(final Context context) {
//        // clear all previous setting error notifications.
//        NotificationUtility.clearAllErrorNotifications(context);
//
//        Zendrive.getZendriveSettings(context, new ZendriveSettingsCallback() {
//            @Override
//            public void onComplete(@Nullable ZendriveSettings zendriveSettings) {
//                if (zendriveSettings == null) {
//                    // The callback returns null if the SDK is not setup.
//                    return;
//                }
//
//                // Handle errors
//                for (ZendriveSettingError error : zendriveSettings.errors) {
//                    switch (error.type) {
//                        case POWER_SAVER_MODE_ENABLED: {
//                            Notification notification =
//                                    NotificationUtility.
//                                            getPSMNotification(context, true);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                        case BACKGROUND_RESTRICTION_ENABLED: {
//                            Notification notification =
//                                    NotificationUtility.getBackgroundRestrictedNotification(context);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.BACKGROUND_RESTRICTED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                        case GOOGLE_PLAY_SETTINGS_ERROR: {
//                            GooglePlaySettingsError e = (GooglePlaySettingsError) error;
//                            Notification notification =
//                                    NotificationUtility.
//                                            getGooglePlaySettingsNotification(context,
//                                                    e.googlePlaySettingsResult);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID,
//                                    notification);
//
//                            break;
//                        }
//                        case LOCATION_PERMISSION_DENIED: {
//                            Notification notification =
//                                    NotificationUtility.
//                                            getLocationPermissionDeniedNotification(context);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.LOCATION_PERMISSION_DENIED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                        case LOCATION_SETTINGS_ERROR: {
//                            Notification notification =
//                                    NotificationUtility.getLocationDisabledNotification(context);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.LOCATION_DISABLED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                        case WIFI_SCANNING_DISABLED: {
//                            Notification notification =
//                                    NotificationUtility.getWifiScanningDisabledNotification(context);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.WIFI_SCANNING_DISABLED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                    }
//                }
//
//                // Handle warnings
//                for (ZendriveSettingWarning warning : zendriveSettings.warnings) {
//                    switch (warning.type) {
//                        case POWER_SAVER_MODE_ENABLED: {
//                            Notification notification =
//                                    NotificationUtility.
//                                            getPSMNotification(context, false);
//                            getNotificationManager(context).notify(
//                                    NotificationUtility.PSM_ENABLED_NOTIFICATION_ID,
//                                    notification);
//                            break;
//                        }
//                    }
//                }
//
//            }
//        });
//    }
//
//    void updateZendriveInsurancePeriod(Context context) {
//        ZendriveOperationCallback insuranceCalllback = new ZendriveOperationCallback() {
//            @Override
//            public void onCompletion(ZendriveOperationResult zendriveOperationResult) {
//                if (!zendriveOperationResult.isSuccess()) {
//                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
//                            zendriveOperationResult.getErrorCode().name());
//                }
//            }
//        };
//        InsuranceInfo insuranceInfo = currentlyActiveInsurancePeriod(context);
//        if (insuranceInfo == null) {
//            Log.d(Constants.LOG_TAG_DEBUG, "updateZendriveInsurancePeriod with NO period");
//            ZendriveInsurance.stopPeriod(context, insuranceCalllback);
//        } else if (insuranceInfo.insurancePeriod == 3) {
//            Log.d(Constants.LOG_TAG_DEBUG,
//                    String.format("updateZendriveInsurancePeriod with period %d and id: %s",
//                            insuranceInfo.insurancePeriod,
//                            insuranceInfo.trackingId));
//            ZendriveInsurance.startDriveWithPeriod3(context, insuranceInfo.trackingId,
//                    insuranceCalllback);
//        } else if (insuranceInfo.insurancePeriod == 2) {
//            Log.d(Constants.LOG_TAG_DEBUG,
//                    String.format("updateZendriveInsurancePeriod with period %d and id: %s",
//                            insuranceInfo.insurancePeriod,
//                            insuranceInfo.trackingId));
//            ZendriveInsurance.startDriveWithPeriod2(context, insuranceInfo.trackingId,
//                    insuranceCalllback);
//        } else {
//            Log.d(Constants.LOG_TAG_DEBUG,
//                    String.format("updateZendriveInsurancePeriod with period %d",
//                            insuranceInfo.insurancePeriod));
//            ZendriveInsurance.startPeriod1(context, insuranceCalllback);
//        }
//    }
//
//    private InsuranceInfo currentlyActiveInsurancePeriod(Context context) {
//        TripManager.State state = TripManager.sharedInstance(context).getTripManagerState();
//        if (!state.isUserOnDuty()) {
//            return null;
//        } else if (state.getPassengersInCar() > 0) {
//            return new InsuranceInfo(3, state.getTrackingId());
//        } else if (state.getPassengersWaitingForPickup() > 0) {
//            return new InsuranceInfo(2, state.getTrackingId());
//        } else {
//            return new InsuranceInfo(1, null);
//        }
//    }
//
//    private NotificationManager getNotificationManager(Context context) {
//        return (NotificationManager) context.getSystemService
//                (Context.NOTIFICATION_SERVICE);
//    }
//}
