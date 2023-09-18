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
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.sdk.Fairmatic;
import com.fairmatic.sdk.classes.FairmaticOperationCallback;
import com.fairmatic.sdk.classes.FairmaticOperationResult;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public interface NextFragment{
        void goOffDuty();
    }

    private NextFragment nextFragment;

    public void setOnLoginSuccessListener(NextFragment listener) {
        this.nextFragment= listener;
    }

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
        if (getContext() == null) {
            Toast.makeText(getContext(), "LoginFragment not attached to a host", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!driverId.equals("") && Fairmatic.INSTANCE.isValidInputParameter(driverId)) {
            // Initialize FairmaticSdk
            FairmaticManager.sharedInstance().initializeFairmaticSDK(getContext(), driverId, new FairmaticOperationCallback() {
                @Override
                public void onCompletion(FairmaticOperationResult result) {
                    if (result instanceof FairmaticOperationResult.Failure) {
                        String errorMessage = String.valueOf(((FairmaticOperationResult.Failure) result).getError());
                        Toast.makeText(getContext(), "Sign up Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        if (nextFragment != null) {
                            nextFragment.goOffDuty();
                        }
                        // Save driver information
                        SharedPrefsManager.sharedInstance(getContext()).setDriverId(driverId);
                        Toast.makeText(getContext(), "Successfully initialized Fairmatic SDK", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Please enter a valid driver id", Toast.LENGTH_SHORT).show();
        }
    }
}
