package com.fairmatic.fairmaticsamplejava.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.MainActivity;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.manager.TripManager;

public class OnDutyFragment extends Fragment implements View.OnClickListener {

    private TextView currentStateTextView;

    private TextView currentInsurancePeriodTextView;
    private Button pickupAPassengerButton;
    private Button cancelRequestButton;
    private Button dropAPassengerButton;
    private Button offDutyButton;

    private Button acceptNewRideReqButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_onduty, container, false);

        currentStateTextView = layout.findViewById(R.id.currentStateTextView);

        currentInsurancePeriodTextView = layout.findViewById(R.id.currentInsurancePeriodTextView);

        layout.findViewById(R.id.acceptNewRideReqButton).setOnClickListener(this);

        acceptNewRideReqButton = layout.findViewById(R.id.acceptNewRideReqButton);
        acceptNewRideReqButton.setOnClickListener(this);

        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton);
        pickupAPassengerButton.setOnClickListener(this);

        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton);
        cancelRequestButton.setOnClickListener(this);

        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton);
        dropAPassengerButton.setOnClickListener(this);

        offDutyButton = layout.findViewById(R.id.offDutyButton);
        offDutyButton.setOnClickListener(this);
        refreshUI();
        return layout;
    }

    @SuppressLint("DefaultLocale")
    private void refreshUI() {
        TripManager.State tripManagerState = TripManager.sharedInstance(getContext()).getTripManagerState();
        boolean passengersInCar = tripManagerState.getPassengersInCar();
        boolean passengerWaitingForPickup = tripManagerState.getPassengersWaitingForPickup();

        int insurancePeriod = 0;
        if (passengersInCar) {
            insurancePeriod = 3;
        } else if (passengerWaitingForPickup) {
            insurancePeriod = 2;
        } else if (tripManagerState.isUserOnDuty()) {
            insurancePeriod = 1;
        }

        currentStateTextView.setText(
                String.format("" +
                                "Passengers In Car: %s" +
                                "\nPassengers Waiting For Pickup: %s", passengersInCar,
                        passengerWaitingForPickup));

        currentInsurancePeriodTextView.setText(String.format("Current Insurance Period: %d", insurancePeriod));

        acceptNewRideReqButton.setEnabled(!passengersInCar && !passengerWaitingForPickup);
        pickupAPassengerButton.setEnabled(passengerWaitingForPickup);
        cancelRequestButton.setEnabled(passengerWaitingForPickup);
        dropAPassengerButton.setEnabled(passengersInCar);
        offDutyButton.setEnabled(!passengersInCar && !passengerWaitingForPickup );
    }

    @Override
    public void onClick(View view) {
        TripManager tripManager = TripManager.sharedInstance(getContext());
        if (view.getId() == R.id.acceptNewRideReqButton) {
            Log.d(Constants.LOG_TAG_DEBUG, "acceptNewRideReqButton tapped");
            tripManager.acceptNewPassengerRequest(getContext());
        } else if (view.getId() == R.id.pickupAPassengerButton) {
            Log.d(Constants.LOG_TAG_DEBUG, "pickupAPassengerButton tapped");
            tripManager.pickupAPassenger(getContext());
        } else if (view.getId() == R.id.cancelRequestButton) {
            Log.d(Constants.LOG_TAG_DEBUG, "cancelRequestButton tapped");
            tripManager.cancelARequest(getContext());
        } else if (view.getId() == R.id.dropAPassengerButton) {
            Log.d(Constants.LOG_TAG_DEBUG, "dropAPassengerButton tapped");
            tripManager.dropAPassenger(getContext());
        } else if (view.getId() == R.id.offDutyButton) {
            Log.d(Constants.LOG_TAG_DEBUG, "offDutyButton tapped");
            tripManager.goOffDuty(getContext());
            ((MainActivity) requireActivity()).replaceFragment(new OffDutyFragment());
        }

        refreshUI();
    }
}
