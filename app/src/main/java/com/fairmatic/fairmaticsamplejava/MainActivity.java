package com.fairmatic.fairmaticsamplejava;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.fairmatic.fairmaticsamplejava.fragments.LoginFragment;
import com.fairmatic.fairmaticsamplejava.fragments.OffDutyFragment;
import com.fairmatic.fairmaticsamplejava.fragments.OnDutyFragment;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFirstFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check Fairmatic settings on app resume if there are errors/warnings present
        FairmaticManager.sharedInstance().checkFairmaticSettings(this);
    }

    private void loadFirstFragment() {
        Fragment firstFragment;
        if (SharedPrefsManager.sharedInstance(this).getDriverId() != null) {
            if (SharedPrefsManager.sharedInstance(this).isUserOnDuty()) {
                firstFragment = getOnDutyFragment();
            }
            else {
                firstFragment = getOffDutyFragment();
            }
        }
        else {
            firstFragment = getLoginFragment();
        }
        replaceFragment(firstFragment);
    }

    private void goOffDuty() {
        replaceFragment(getOffDutyFragment());
    }

    // The driver is currently off duty
    private OffDutyFragment getOffDutyFragment() {
        OffDutyFragment offDutyFragment = new OffDutyFragment();
        offDutyFragment.setOnLoginSuccessListener(this::goOnDuty);
        return offDutyFragment;
        //return new OffDutyFragment(() -> goOnDuty());
    }

    private void goOnDuty() {
        replaceFragment(getOnDutyFragment());
    }

    // The driver is currently on duty
    private OnDutyFragment getOnDutyFragment() {
        OnDutyFragment onDutyFragment = new OnDutyFragment();
        onDutyFragment.setOnLoginSuccessListener(this::goOffDuty);
        return onDutyFragment;
        //return new OnDutyFragment(() -> goOffDuty());
    }

    // The driver is yet to log in
    private LoginFragment getLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setOnLoginSuccessListener(this::goOffDuty);
        return loginFragment;
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainContentView, newFragment);
        ft.commit();
    }
}
