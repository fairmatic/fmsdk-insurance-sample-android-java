package com.fairmatic.fairmaticsamplejava.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

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

    public synchronized void acceptNewPassengerRequest(Context context, FairmaticOperationCallback callback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod2(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to accept new passenger",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.passenegersWaitingForPickup = true;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
            }
            callback.onCompletion(fairmaticOperationResult);
        });
    }

    public synchronized void pickupAPassenger(Context context, FairmaticOperationCallback callback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod3(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to pickup a passenger",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.passenegersWaitingForPickup = false;
                state.passenegersInCar = true;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);

                SharedPrefsManager.sharedInstance(context)
                        .setPassengersInCar(state.passenegersInCar);
            }
            callback.onCompletion(fairmaticOperationResult);
        });
    }

    public synchronized void cancelARequest(Context context, FairmaticOperationCallback callback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to cancel a request",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.passenegersWaitingForPickup = false;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
            }
            callback.onCompletion(fairmaticOperationResult);
        } );
    }

    public synchronized void dropAPassenger(Context context, FairmaticOperationCallback callback) {

        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to drop a passenger",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.passenegersInCar = false;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengersInCar(state.passenegersInCar);
            }
            callback.onCompletion(fairmaticOperationResult);
        });

    }

    public synchronized void goOnDuty(Context context) {

        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to go on duty",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.isUserOnDuty = true;
                Log.d("Check123", "goOnDuty: " + state.isUserOnDuty);
                SharedPrefsManager.sharedInstance(context).setIsUserOnDuty(state.isUserOnDuty);
            }
        });
    }

    public synchronized void goOffDuty(Context context, FairmaticOperationCallback callback) {

        FairmaticManager.sharedInstance().handleStopPeriod(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to go off duty",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.isUserOnDuty = false;
                SharedPrefsManager.sharedInstance(context).setIsUserOnDuty(state.isUserOnDuty);
            }
            callback.onCompletion(fairmaticOperationResult);
        });
    }

    public synchronized State getTripManagerState() {
        return new State(state);
    }
}
