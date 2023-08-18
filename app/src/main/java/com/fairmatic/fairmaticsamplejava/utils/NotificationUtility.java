package com.fairmatic.fairmaticsamplejava.utils;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.MainActivity;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.SettingsCheckActivity;
import com.google.android.gms.location.LocationSettingsResult;

public class NotificationUtility {


    public static int LOCATION_PERMISSION_DENIED_NOTIFICATION_ID = 100;
    public static int LOCATION_DISABLED_NOTIFICATION_ID = 101;
    public static int PSM_ENABLED_NOTIFICATION_ID = 103;
    public static int BACKGROUND_RESTRICTED_NOTIFICATION_ID = 105;
    public static int WIFI_SCANNING_DISABLED_NOTIFICATION_ID = 107;
    public static int GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID = 108;
    private static int FAIRMATIC_FAILED_NOTIFICATION_ID = 4;

    private static final int PSM_ENABLED_REQUEST_CODE = 200;
    private static final int LOCATION_DISABLED_REQUEST_CODE = 201;
    private static final int BACKGROUND_RESTRICTED_REQUEST_CODE = 202;
    private static final int WIFI_SCANNING_REQUEST_CODE = 204;
    private static final int GOOGLE_PLAY_SETTINGS_REQUEST_CODE = 205;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 206;


    private static final String FOREGROUND_CHANNEL_KEY = "Foreground";
    private static final String ISSUES_CHANNEL_KEY = "Issues";

    /**
     * Creates a notification to be displayed when the SDK is tracking a trip.
     */
    public static Notification getInDriveNotification(@NonNull Context context) {
        createNotificationChannels(context);
        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Drive Active.").build();
    }

    /**
     * Creates a notification to be displayed when the SDK is detecting a potential trip.
     */
    public static Notification getMaybeInDriveNotification(@NonNull Context context) {
        createNotificationChannels(context);
        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Detecting Possible Drive.").build();
    }

    /**
     * Creates a notification to be displayed when Power saver mode is enabled
     * on the device.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1) // This error shouldn't be sent below this.
    public static Notification getPSMNotification(Context context, boolean isError) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, PSM_ENABLED_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String titleTickerPrefix = isError ? "Error:" : "Warning:";

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle(titleTickerPrefix + " Power Saver Mode Enabled")
                .setTicker(titleTickerPrefix + " power Saver Mode Enabled")
                .setContentText("Disable power saver mode.")
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.notification_icon)
                .build();
    }

    /**
     * Creates a notification to be displayed when background restrictions are enabled for
     * the application.
     */
    @TargetApi(Build.VERSION_CODES.P)
    public static Notification getBackgroundRestrictedNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, BACKGROUND_RESTRICTED_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Background Restricted")
                .setTicker("Background Restricted")
                .setContentText("Disable Background Restriction")
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .build();
    }

    /**
     * Creates a notification to be displayed when location permission is denied to the application.
     */
    public static Notification getLocationPermissionDeniedNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(context, SettingsCheckActivity.class);
        actionIntent.setAction(Constants.EVENT_LOCATION_PERMISSION_ERROR);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, LOCATION_PERMISSION_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Location Permission Denied")
                .setTicker("Location Permission Denied")
                .setContentText("Grant location permission to Fairmatic Test")
                .setSmallIcon(R.drawable.notification_icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    /**
     * Creates a notification to be displayed when high accuracy location is disabled on the device.
     */
    public static Notification getLocationDisabledNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, LOCATION_DISABLED_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Location Disabled")
                .setTicker("Location Disabled")
                .setContentIntent(pi)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentText("Enable settings for location.")
                .setSmallIcon(R.drawable.notification_icon)
                .build();
    }

    /**
     * Creates a notification to be displayed when a Google Play Settings Error
     * is reported by the Fairmatic SDK.
     */
    public static Notification getGooglePlaySettingsNotification(Context context,
                                                                 LocationSettingsResult result) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(context, SettingsCheckActivity.class);
        actionIntent.setAction(Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR);
        actionIntent.putExtra("DATA", result);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, GOOGLE_PLAY_SETTINGS_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Google Play Settings Error")
                .setTicker("Google Play Settings Error")
                .setContentText("Tap here to resolve.")
                .setSmallIcon(R.drawable.notification_icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    /**
     * Creates a notification to be displayed when wifi scanning is disabled on the device.
     */
    public static Notification getWifiScanningDisabledNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, WIFI_SCANNING_REQUEST_CODE,
                actionIntent, FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Wifi Scanning Disabled")
                .setTicker("Wifi Scanning Disabled")
                .setContentText("Tap to enable wifi radio.")
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .build();
    }

    public static void displayFairmaticSetupFailureNotification(Context context) {
        createNotificationChannels(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.NOTIFICATION_ID, FAIRMATIC_FAILED_NOTIFICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context.getApplicationContext(), ISSUES_CHANNEL_KEY)
                .setContentTitle("Failed To Enable Insurance Benefits")
                .setTicker("Failed To Enable Insurance Benefits")
                .setContentText("Tap This Notification To Retry")
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifBuilder.setCategory(Notification.CATEGORY_ERROR);
        }
        Notification notification = notifBuilder.build();

        // Display notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(FAIRMATIC_FAILED_NOTIFICATION_ID, notification);
        }
    }

    public static void hideFairmaticSetupFailureNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(FAIRMATIC_FAILED_NOTIFICATION_ID);
        }
    }

    public static void clearAllErrorNotifications(Context context) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(LOCATION_PERMISSION_DENIED_NOTIFICATION_ID);
        manager.cancel(PSM_ENABLED_NOTIFICATION_ID);
        manager.cancel(BACKGROUND_RESTRICTED_NOTIFICATION_ID);
        manager.cancel(WIFI_SCANNING_DISABLED_NOTIFICATION_ID);
        manager.cancel(GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID);
    }

    private static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel foregroundNotificationChannel = new NotificationChannel
                    (FOREGROUND_CHANNEL_KEY, "Fairmatic trip tracking",
                            NotificationManager.IMPORTANCE_DEFAULT);
            foregroundNotificationChannel.setShowBadge(false);

            NotificationChannel issuesNotificationChannel = new NotificationChannel
                    (ISSUES_CHANNEL_KEY, "Issues",
                            NotificationManager.IMPORTANCE_DEFAULT);
            issuesNotificationChannel.setShowBadge(true);

            if (manager != null) {
                manager.createNotificationChannel(foregroundNotificationChannel);
                manager.createNotificationChannel(issuesNotificationChannel);
            }
        }
    }
}
