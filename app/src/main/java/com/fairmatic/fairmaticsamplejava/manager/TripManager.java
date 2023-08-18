package com.fairmatic.fairmaticsamplejava.manager;

import android.content.Context;

public class TripManager {

    public class State {
        private boolean isUserOnDuty;
        private boolean passenegersWaitingForPickup;
        private boolean passenegersInCar;
        private String trackingId;

        public boolean isUserOnDuty() {
            return isUserOnDuty;
        }

        public boolean getPassengersWaitingForPickup() {
            return passenegersWaitingForPickup;
        }

        public boolean getPassengersInCar() {
            return passenegersInCar;
        }

        String getTrackingId() {
            return trackingId;
        }

        State(boolean isUserOnDuty, boolean passenegersWaitingForPickup,
              boolean passenegersInCar, String trackingId) {
            this.isUserOnDuty = isUserOnDuty;
            this.passenegersWaitingForPickup = passenegersWaitingForPickup;
            this.passenegersInCar = passenegersInCar;
            this.trackingId = trackingId;
        }

        State(State another) {
            this.isUserOnDuty = another.isUserOnDuty;
            this.passenegersWaitingForPickup = another.passenegersWaitingForPickup;
            this.passenegersInCar = another.passenegersInCar;
            this.trackingId = another.trackingId;
        }
    }

    private static TripManager sharedInstance;

    public static synchronized TripManager sharedInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new TripManager(context);
        }
        return sharedInstance;
    }

    private State state;
    private TripManager(Context context) {
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.sharedInstance(context);
        state = new State(sharedPrefsManager.isUserOnDuty(),
                sharedPrefsManager.passengersWaitingForPickup(),
                sharedPrefsManager.passengersInCar(),
                sharedPrefsManager.getTrackingId());
    }

    public synchronized void acceptNewPassengerRequest(Context context) {
        state.passenegersWaitingForPickup = true;
        SharedPrefsManager.sharedInstance(context)
                .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    public synchronized void pickupAPassenger(Context context) {
        state.passenegersWaitingForPickup = false;
        SharedPrefsManager.sharedInstance(context)
                .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);

        state.passenegersInCar = true;
        SharedPrefsManager.sharedInstance(context)
                .setPassengersInCar(state.passenegersInCar);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    public synchronized void cancelARequest(Context context) {
        state.passenegersWaitingForPickup = false;
        SharedPrefsManager.sharedInstance(context)
                .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    public synchronized void dropAPassenger(Context context) {
        state.passenegersInCar = false;
        SharedPrefsManager.sharedInstance(context).setPassengersInCar(state.passenegersInCar);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    public synchronized void goOnDuty(Context context) {
        state.isUserOnDuty = true;
        SharedPrefsManager.sharedInstance(context).setIsUserOnDuty(state.isUserOnDuty);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    public synchronized void goOffDuty(Context context) {
        state.isUserOnDuty = false;
        SharedPrefsManager.sharedInstance(context).setIsUserOnDuty(state.isUserOnDuty);
        updateTrackingIdIfNeeded(context);
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(context);
    }

    private void updateTrackingIdIfNeeded(Context context) {
        if (state.passenegersWaitingForPickup || state.passenegersInCar ) {
            // We need trackingId
            if (state.trackingId == null) {
                state.trackingId = ((Long)System.currentTimeMillis()).toString();
                SharedPrefsManager.sharedInstance(context).setTrackingId(state.trackingId);
            }
        }
        else {
            state.trackingId = null;
            SharedPrefsManager.sharedInstance(context).setTrackingId(state.trackingId);
        }
    }

    public synchronized State getTripManagerState() {
        return new State(state);
    }
}
