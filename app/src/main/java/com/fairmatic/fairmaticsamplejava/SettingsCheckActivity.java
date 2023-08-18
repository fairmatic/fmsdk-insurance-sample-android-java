package com.fairmatic.fairmaticsamplejava;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fairmatic.fairmaticsamplejava.utils.NotificationUtility;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class SettingsCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveSettingsErrors();
    }

    private void resolveSettingsErrors() {
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            return;
        }

        if (Constants.EVENT_LOCATION_PERMISSION_ERROR.equals(intent.getAction())) {
            requestLocationPermission();
        }

        if (Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR.equals(intent.getAction())) {
            LocationSettingsResult r = intent.getParcelableExtra("DATA");
            resolveGooglePlaySettings(r);
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST);
        }
    }

    private void resolveGooglePlaySettings(LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // Should not happen
                Log.d(Constants.LOG_TAG_DEBUG, "Success received when expected" +
                        "error from Google Play " +
                        "Services");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            this,
                            Constants.GOOGLE_PLAY_SERVICES_REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(
                            Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NotificationUtility.
                        LOCATION_PERMISSION_DENIED_NOTIFICATION_ID);
            }
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
