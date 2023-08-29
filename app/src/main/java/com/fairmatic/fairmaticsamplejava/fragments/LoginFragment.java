package com.fairmatic.fairmaticsamplejava.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fairmatic.fairmaticsamplejava.MainActivity;
import com.fairmatic.fairmaticsamplejava.R;
import com.fairmatic.fairmaticsamplejava.SampleAppApplication;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText idEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        idEditText = layout.findViewById(R.id.idEditText);
        layout.findViewById(R.id.signupButton).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signupButton) {
            signupButtonClicked();
        }
    }

    private void signupButtonClicked() {
        final String driverId = idEditText.getText().toString();
        if (!driverId.equals("")) {
            // Save driver information
            SharedPrefsManager.sharedInstance(getContext()).setDriverId(driverId);
            // Initialize FairmaticSdk
            FairmaticManager.sharedInstance().initializeFairmaticSDK(getContext(), driverId, new FairmaticOperationCallback() {
                @Override
                public void onCompletion(FairmaticOperationResult result) {
                    if (result instanceof FairmaticOperationResult.Failure) {
                        String errorMessage = String.valueOf(((FairmaticOperationResult.Failure) result).getError());
                        Toast.makeText(getContext(), "Failed to initialize Fairmatic SDK : " + errorMessage, Toast.LENGTH_SHORT).show();

                    } else {
                        ((MainActivity) requireActivity()).replaceFragment(new OffDutyFragment());
                        Toast.makeText(getContext(), "Successfully initialized Fairmatic SDK", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Load UI

        }
    }
}
