package com.fairmatic.fairmaticsamplejava.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.MainActivity;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.manager.TripManager;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

import java.util.Objects;

public class OffDutyFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_offduty, container, false);
        layout.findViewById(R.id.goOnDutyButton).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.goOnDutyButton) {
            goOnDutyButtonClicked();
        }
    }

    private void goOnDutyButtonClicked() {
        Log.d(Constants.LOG_TAG_DEBUG, "goOnDutyButtonClicked");
        TripManager.sharedInstance(getContext()).goOnDuty(getContext(), new FairmaticOperationCallback() {
            @Override
            public void onCompletion(@NonNull FairmaticOperationResult fairmaticOperationResult) {
                if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                    String errorMessage = String.valueOf(((FairmaticOperationResult.Failure) fairmaticOperationResult).getError());
                    Log.d(Constants.LOG_TAG_DEBUG, "Failed to go on duty : " + errorMessage);
                } else {
                    Log.d(Constants.LOG_TAG_DEBUG, "Successfully went on duty");
                    ((MainActivity) requireActivity()).replaceFragment(new OnDutyFragment());
                }
            }
        });
    }
}
