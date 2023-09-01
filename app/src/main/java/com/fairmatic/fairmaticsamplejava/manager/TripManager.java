package com.fairmatic.fairmaticsamplejava.manager;

import android.content.Context;
import android.widget.Toast;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

public class TripManager {

    public class State {
        private boolean isUserOnDuty;
        private boolean passenegerWaitingForPickup;
        private boolean passenegerInCar;
        private String trackingId;

        public boolean isUserOnDuty() {
            return isUserOnDuty;
        }

        public boolean getPassengerWaitingForPickup() {
            return passenegerWaitingForPickup;
        }

        public boolean getPassengerInCar() {
            return passenegerInCar;
        }

        String getTrackingId() {
            return trackingId;
        }

        State(boolean isUserOnDuty, boolean passenegersWaitingForPickup,
              boolean passenegersInCar, String trackingId) {
            this.isUserOnDuty = isUserOnDuty;
            this.passenegerWaitingForPickup = passenegersWaitingForPickup;
            this.passenegerInCar = passenegersInCar;
            this.trackingId = trackingId;
        }

        State(State another) {
            this.isUserOnDuty = another.isUserOnDuty;
            this.passenegerWaitingForPickup = another.passenegerWaitingForPickup;
            this.passenegerInCar = another.passenegerInCar;
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
                sharedPrefsManager.passengerWaitingForPickup(),
                sharedPrefsManager.passengerInCar(),
                sharedPrefsManager.getTrackingId());
    }

    public synchronized void acceptNewPassengerRequest(Context context, FairmaticOperationCallback callback) {
        FairmaticManager.sharedInstance().handleInsurancePeriod2(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to accept new passenger",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.passenegerWaitingForPickup = true;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengerWaitingForPickup(state.passenegerWaitingForPickup);
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
                state.passenegerWaitingForPickup = false;
                state.passenegerInCar = true;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengerWaitingForPickup(state.passenegerWaitingForPickup);

                SharedPrefsManager.sharedInstance(context)
                        .setPassengerInCar(state.passenegerInCar);
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
                state.passenegerWaitingForPickup = false;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengerWaitingForPickup(state.passenegerWaitingForPickup);
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
                state.passenegerInCar = false;
                SharedPrefsManager.sharedInstance(context)
                        .setPassengerInCar(state.passenegerInCar);
            }
            callback.onCompletion(fairmaticOperationResult);
        });

    }

    public synchronized void goOnDuty(Context context, FairmaticOperationCallback callback) {

        FairmaticManager.sharedInstance().handleInsurancePeriod1(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Toast.makeText(context, "Failed to go on duty",
                        Toast.LENGTH_SHORT).show();
            } else {
                state.isUserOnDuty = true;
                SharedPrefsManager.sharedInstance(context).setIsUserOnDuty(state.isUserOnDuty);
            }
            callback.onCompletion(fairmaticOperationResult);
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
