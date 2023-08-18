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
        TripManager.sharedInstance(getContext()).goOnDuty(getContext());
        ((MainActivity) requireActivity()).replaceFragment(new OnDutyFragment());
    }
}
