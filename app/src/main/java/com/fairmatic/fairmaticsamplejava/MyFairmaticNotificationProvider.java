package com.fairmatic.fairmaticsamplejava;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fairmatic.fairmaticsamplejava.utils.NotificationUtility;
import com.fairmatic.sdk.classes.FairmaticNotificationContainer;
import com.fairmatic.sdk.classes.FairmaticNotificationProvider;

public class MyFairmaticNotificationProvider implements FairmaticNotificationProvider {

    private static final int ZENDRIVE_NOTIFICATION_ID = 495;
    @NonNull
    @Override
    public FairmaticNotificationContainer getInDriveNotificationContainer(@NonNull Context context) {
        return new FairmaticNotificationContainer(
                ZENDRIVE_NOTIFICATION_ID,
                NotificationUtility.getInDriveNotification(context));
    }

    @NonNull
    @Override
    public FairmaticNotificationContainer getMaybeInDriveNotificationContainer(@NonNull Context context) {
        return new FairmaticNotificationContainer(
                ZENDRIVE_NOTIFICATION_ID,
                NotificationUtility.getMaybeInDriveNotification(context));
    }

    @Nullable
    @Override
    public FairmaticNotificationContainer getWaitingForDriveNotificationContainer(@NonNull Context context) {
        return null;
    }
}
