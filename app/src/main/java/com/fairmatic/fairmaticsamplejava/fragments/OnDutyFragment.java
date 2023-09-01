package com.fairmatic.fairmaticsamplejava.fragments;

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
import com.fairmatic.fairmaticsamplejava.MainActivity;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.TripManager;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

public class OnDutyFragment extends Fragment {

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

        acceptNewRideReqButton = layout.findViewById(R.id.acceptNewRideReqButton);

        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton);

        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton);

        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton);

        offDutyButton = layout.findViewById(R.id.offDutyButton);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshUI();
        FairmaticManager.sharedInstance().updateFairmaticInsurancePeriod(getContext());

        acceptNewRideReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG_DEBUG, "acceptNewRideReqButton tapped");
                TripManager tripManager = getContext() != null ? TripManager.sharedInstance(getContext()) : null;
                if (tripManager != null) {
                    refreshUIForPeriod2();
                    tripManager.acceptNewPassengerRequest(getContext(), new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(FairmaticOperationResult fairmaticOperationResult) {
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 2");
                            }
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                            }
                        }
                    });
                }
            }
        });

        pickupAPassengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG_DEBUG, "pickupAPassengerButton tapped");
                TripManager tripManager = getContext() != null ? TripManager.sharedInstance(getContext()) : null;
                if (tripManager != null) {
                    refreshUIForPeriod3();
                    tripManager.pickupAPassenger(getContext(), new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(FairmaticOperationResult fairmaticOperationResult) {
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 3");
                            }
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                            }
                        }
                    });
                }
            }
        });

        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG_DEBUG, "cancelRequestButton tapped");
                TripManager tripManager = getContext() != null ? TripManager.sharedInstance(getContext()) : null;
                if (tripManager != null) {
                    refreshUIForPeriod1();
                    tripManager.cancelARequest(getContext(), new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(FairmaticOperationResult fairmaticOperationResult) {
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1");
                            }
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                            }
                        }
                    });
                }
            }
        });

        dropAPassengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG_DEBUG, "dropAPassengerButton tapped");
                TripManager tripManager = getContext() != null ? TripManager.sharedInstance(getContext()) : null;
                if (tripManager != null) {
                    refreshUIForPeriod1();
                    tripManager.dropAPassenger(getContext(), new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(FairmaticOperationResult fairmaticOperationResult) {
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switched to 1");
                            }
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period switch failed, error: " +
                                        ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                            }
                        }
                    });
                }
            }
        });

        offDutyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG_DEBUG, "offDutyButton tapped");
                Toast.makeText(getContext(), "Going off duty", Toast.LENGTH_SHORT).show();
                TripManager tripManager = getContext() != null ? TripManager.sharedInstance(getContext()) : null;
                if (tripManager != null) {
                    ((MainActivity) getActivity()).replaceFragment(new OffDutyFragment());
                    tripManager.goOffDuty(getContext(), new FairmaticOperationCallback() {
                        @Override
                        public void onCompletion(FairmaticOperationResult fairmaticOperationResult) {
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Success) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Insurance period stopped");
                            }
                            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                                Log.d(Constants.LOG_TAG_DEBUG, "Going Off duty failed, error: " +
                                        ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError().name());
                            }
                        }
                    });
                }
            }
        });

    }

    private void refreshUI() {
        TripManager tripManager = null;
        if (getContext() != null) {
            tripManager = TripManager.sharedInstance(getContext());
        }

        FairmaticManager fairmaticManager = FairmaticManager.sharedInstance();

        if (tripManager != null && fairmaticManager != null) {
            TripManager.State state = tripManager.getTripManagerState();
            Log.d("check123", "state: " + state.isUserOnDuty());
            if (state != null && state.isUserOnDuty()) {
                if (state.getPassengerWaitingForPickup()) {
                    refreshUIForPeriod2();
                } else if (state.getPassengerInCar()) {
                    refreshUIForPeriod3();
                } else {
                    refreshUIForPeriod1();
                }
            }
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
