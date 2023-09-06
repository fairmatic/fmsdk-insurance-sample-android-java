package com.fairmatic.fairmaticsamplejava;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.fairmatic.fairmaticsamplejava.fragments.LoginFragment;
import com.fairmatic.fairmaticsamplejava.fragments.OffDutyFragment;
import com.fairmatic.fairmaticsamplejava.fragments.OnDutyFragment;
import com.fairmatic.fairmaticsamplejava.manager.FairmaticManager;
import com.fairmatic.fairmaticsamplejava.manager.SharedPrefsManager;
import com.fairmatic.fairmaticsamplejava.manager.TripManager;

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
        FairmaticManager.sharedInstance().maybeCheckFairmaticSettings(this);
    }

    private void loadFirstFragment() {
        Fragment firstFragment;
        if (SharedPrefsManager.sharedInstance(this).getDriverId() != null) {
            if (TripManager.sharedInstance(this).getTripManagerState().isUserOnDuty()) {
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
        return new OffDutyFragment(() -> goOnDuty());
    }

    private void goOnDuty() {
        replaceFragment(new OnDutyFragment(() -> goOffDuty()));
    }

    // The driver is currently on duty
    private OnDutyFragment getOnDutyFragment() {
        return new OnDutyFragment(() -> goOffDuty());
    }

    // The driver is yet to log in
    private LoginFragment getLoginFragment() {
        return new LoginFragment(() -> goOffDuty());
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainContentView, newFragment);
        ft.commit();
    }
}
