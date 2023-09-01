package com.fairmatic.fairmaticsamplejava.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("ApplySharedPref")
public class SharedPrefsManager {
    private SharedPreferences prefs;
    private static SharedPrefsManager sharedInstance;

    private static final String DRIVER_ID = "driverId";
    private static final String USER_ON_DUTY = "isUserOnDuty";
    private static final String PASSENGER_IN_CAR = "passengerInCar";
    private static final String PASSENGER_WAITING_FOR_PICKUP = "passengerWaitingForPickup";
    private static final String TRACKING_ID = "trackingId";
    private static final String FAIRMATIC_SETTINGS_ERRORS = "errorsFound";
    private static final String FAIRMATIC_SETTINGS_WARNINGS = "warningsFound";
    private static final String RETRY_FAIRMATIC_SETUP = "retry_fairmatic_setup";

    public static synchronized SharedPrefsManager sharedInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new SharedPrefsManager(context);
        }
        return sharedInstance;
    }

    private SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences("com.fairmatic.fairmaticsamplejava", Context.MODE_PRIVATE);
    }

    public String getDriverId() {
        return prefs.getString(DRIVER_ID, null);
    }

    public void setDriverId(String driverId) {
        prefs.edit().putString(DRIVER_ID, driverId).apply();
    }

    Boolean isUserOnDuty() {
        return prefs.getBoolean(USER_ON_DUTY, false);
    }

    void setIsUserOnDuty(boolean isUserOnDuty) {
        prefs.edit().putBoolean(USER_ON_DUTY, isUserOnDuty).apply();
    }

    Boolean passengerInCar() {
        return prefs.getBoolean(PASSENGER_IN_CAR, false);
    }

    void setPassengerInCar(boolean passengerInCar) {
        prefs.edit().putBoolean(PASSENGER_IN_CAR, passengerInCar).apply();
    }

    Boolean passengerWaitingForPickup() {
        return prefs.getBoolean(PASSENGER_WAITING_FOR_PICKUP, false);
    }

    void setPassengerWaitingForPickup(boolean passengerWaitingForPickup) {
        prefs.edit().putBoolean(PASSENGER_WAITING_FOR_PICKUP, passengerWaitingForPickup).apply();
    }

    String getTrackingId() {
        return prefs.getString(TRACKING_ID, null);
    }

    void setTrackingId(String trackingId) {
        prefs.edit().putString(TRACKING_ID, trackingId).apply();
    }

    public boolean isSettingsErrorFound() {
        return prefs.getBoolean(FAIRMATIC_SETTINGS_ERRORS, false);
    }

    public void setSettingsErrorsFound(boolean errorsFound) {
        prefs.edit().putBoolean(FAIRMATIC_SETTINGS_ERRORS, errorsFound).apply();
    }

    public boolean isSettingsWarningsFound() {
        return prefs.getBoolean(FAIRMATIC_SETTINGS_WARNINGS, false);
    }

    public void setSettingsWarningsFound(boolean warningsFound) {
        prefs.edit().putBoolean(FAIRMATIC_SETTINGS_WARNINGS, warningsFound).apply();
    }

    public boolean shouldRetryFairmaticSetup() {
        return prefs.getBoolean(RETRY_FAIRMATIC_SETUP, false);
    }

    public void setRetryFairmaticSetup(boolean retry) {
        prefs.edit().putBoolean(RETRY_FAIRMATIC_SETUP, retry).apply();
    }
}
