package com.fairmatic.fairmaticsamplejava.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

public class OnDutyFragment extends Fragment {

    private TextView currentStateTextView;

    private TextView currentInsurancePeriodTextView;
    private Button pickupAPassengerButton;
    private Button cancelRequestButton;
    private Button dropAPassengerButton;
    private Button offDutyButton;

    private Button acceptNewRideReqButton;

    private SharedPrefsManager sharedPrefsManager;
    public interface NextFragment{
        void goOffDuty();
    }

    private NextFragment nextFragment;

    public void setOnLoginSuccessListener( NextFragment listener) {
        this.nextFragment= listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_onduty, container, false);

        currentStateTextView = layout.findViewById(R.id.currentStateTextView);

        currentInsurancePeriodTextView = layout.findViewById(R.id.currentInsurancePeriodTextView);

        acceptNewRideReqButton = layout.findViewById(R.id.acceptNewRideReqButton);

        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton);

        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton);

        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton);

        offDutyButton = layout.findViewById(R.id.offDutyButton);
        refreshUI();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        sharedPrefsManager = SharedPrefsManager.sharedInstance(context);

        acceptNewRideReqButton.setOnClickListener(view -> {
            Log.d(Constants.LOG_TAG_DEBUG, "acceptNewRideReqButton tapped");
            FairmaticManager.sharedInstance().startInsurancePeriod2(context, fairmaticOperationResult -> {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Failed to accept a new ride request, error: " +
                            ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 2");
                }
            });
            sharedPrefsManager.setPassengerWaitingForPickup(true);
            refreshUIForPeriod2();
        });

        pickupAPassengerButton.setOnClickListener(view -> {
            Log.d(Constants.LOG_TAG_DEBUG, "pickupAPassengerButton tapped");
            FairmaticManager.sharedInstance().startInsurancePeriod3(context, fairmaticOperationResult -> {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                   Log.d(Constants.LOG_TAG_DEBUG, "Failed to pickup a passenger, error: " +
                           ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 3");
                }
            });

            sharedPrefsManager.setPassengerWaitingForPickup(false);
            sharedPrefsManager.setPassengerInCar(true);
            refreshUIForPeriod3();
        });

        cancelRequestButton.setOnClickListener(view -> {
            Log.d(Constants.LOG_TAG_DEBUG, "cancelRequestButton tapped");
            FairmaticManager.sharedInstance().startInsurancePeriod1(context, fairmaticOperationResult -> {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                   Log.d(Constants.LOG_TAG_DEBUG, "Failed to cancel a request, error: " +
                           ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1");

                }
            } );
            sharedPrefsManager.setPassengerWaitingForPickup(false);
            refreshUIForPeriod1();
        });

        dropAPassengerButton.setOnClickListener(view -> {
            Log.d(Constants.LOG_TAG_DEBUG, "dropAPassengerButton tapped");
            FairmaticManager.sharedInstance().startInsurancePeriod1(context, fairmaticOperationResult -> {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Failed to cancel a request, error: " +
                            ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG, "Insurance Perod switched to 1");
                }
            });
            sharedPrefsManager.setPassengerInCar(false);
            refreshUIForPeriod1();
        });

        offDutyButton.setOnClickListener(view -> {
            Log.d(Constants.LOG_TAG_DEBUG, "offDutyButton tapped");
            Toast.makeText(getContext(), "Going off duty", Toast.LENGTH_SHORT).show();
            FairmaticManager.sharedInstance().stopPeriod(context, fairmaticOperationResult -> {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                    Log.d(Constants.LOG_TAG_DEBUG, "Going Off duty failed, error: " +
                            ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG,"Insurance Period Stopped, goind off duty");
                }
            });
            sharedPrefsManager.setIsUserOnDuty(false);
            nextFragment.goOffDuty();
        });

    }

    private void refreshUI() {
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.sharedInstance(requireContext());
        if (sharedPrefsManager.passengerWaitingForPickup()) {
            refreshUIForPeriod2();
        } else if (sharedPrefsManager.passengerInCar()) {
            refreshUIForPeriod3();
        } else {
            refreshUIForPeriod1();
        }

    }

    private void refreshUIForPeriod1() {
        currentStateTextView.setText(
                "Passenger In Car:  FALSE\n" +
                        "Passenger Waiting For Pickup:  FALSE"
        );
        currentInsurancePeriodTextView.setText("Insurance Period: 1");
        acceptNewRideReqButton.setEnabled(true);
        pickupAPassengerButton.setEnabled(false);
        cancelRequestButton.setEnabled(false);
        dropAPassengerButton.setEnabled(false);
        offDutyButton.setEnabled(true);
    }

    private void refreshUIForPeriod2() {
        currentStateTextView.setText(
                "Passenger In Car:  FALSE\n" +
                        "Passenger Waiting For Pickup:  TRUE"
        );
        currentInsurancePeriodTextView.setText("Insurance Period: 2");
        acceptNewRideReqButton.setEnabled(false);
        pickupAPassengerButton.setEnabled(true);
        cancelRequestButton.setEnabled(true);
        dropAPassengerButton.setEnabled(false);
        offDutyButton.setEnabled(false);
    }

    private void refreshUIForPeriod3() {
        currentStateTextView.setText(
                "Passenger In Car:  TRUE\n" +
                        "Passenger Waiting For Pickup:  FALSE"
        );
        currentInsurancePeriodTextView.setText("Insurance Period: 3");
        acceptNewRideReqButton.setEnabled(false);
        pickupAPassengerButton.setEnabled(false);
        cancelRequestButton.setEnabled(false);
        dropAPassengerButton.setEnabled(true);
        offDutyButton.setEnabled(false);
    }

}
