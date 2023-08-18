package com.fairmatic.fairmaticsamplejava;

import android.content.Context;
//import android.support.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fairmatic.fairmaticsamplejava.utils.NotificationUtility;
import com.zendrive.sdk.ZendriveNotificationContainer;
import com.zendrive.sdk.ZendriveNotificationProvider;

public class MyZendriveNotificationProvider implements ZendriveNotificationProvider {
    private static final int ZENDRIVE_NOTIFICATION_ID = 495;

    @Nullable
    @Override
    public ZendriveNotificationContainer getWaitingForDriveNotificationContainer(@androidx.annotation.NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public ZendriveNotificationContainer getMaybeInDriveNotificationContainer(@NonNull Context context) {
        return new ZendriveNotificationContainer(
                ZENDRIVE_NOTIFICATION_ID,
                NotificationUtility.getMaybeInDriveNotification(context));
    }

    @NonNull
    @Override
    public ZendriveNotificationContainer getInDriveNotificationContainer(@NonNull Context context) {
        return new ZendriveNotificationContainer(
                ZENDRIVE_NOTIFICATION_ID,
                NotificationUtility.getInDriveNotification(context));
    }
}
