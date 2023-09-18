package com.fairmatic.fairmaticsamplejava.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fairmatic.fairmaticsamplejava.Constants;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

import java.util.Objects;

public class OffDutyFragment extends Fragment implements View.OnClickListener {

    public interface NextFragment{
        void goOnDuty();
    }

    private NextFragment nextFragment;

    public void setOnLoginSuccessListener(NextFragment listener) {
        this.nextFragment= listener;
    }

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
        Log.d(Constants.LOG_TAG_DEBUG, "Go On Duty Button Clicked");
        Context context = getContext();
        Toast.makeText(context, "Going on duty", Toast.LENGTH_SHORT).show();

        if (context == null) {
            Toast.makeText(context, "OffDutyFragment not attached to a host", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        FairmaticManager.sharedInstance().startInsurancePeriod1(context, fairmaticOperationResult -> {
            if (fairmaticOperationResult instanceof FairmaticOperationResult.Failure) {
                Log.d(Constants.LOG_TAG_DEBUG, "Failed to handle insurance period 1 : " + ((FairmaticOperationResult.Failure) fairmaticOperationResult).getError());
            } else {
                Log.d(Constants.LOG_TAG_DEBUG, "Start period 1 successfully");
            }
        });

        SharedPrefsManager.sharedInstance(requireContext()).setIsUserOnDuty(true);
        if (nextFragment != null) {
            nextFragment.goOnDuty();
        }
    }
}
