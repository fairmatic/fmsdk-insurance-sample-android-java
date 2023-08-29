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
//import com.fairmatic.fairmaticsamplejava.manager.ZendriveManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadFirstFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check Zendrive settings on app resume if there are errors/warnings present
        FairmaticManager.sharedInstance().maybeCheckFairmaticSettings(this);
        loadFirstFragment();
        FairmaticManager.sharedInstance().maybeCheckFairmaticSettings(this);
    }

    private void loadFirstFragment() {
        Fragment firstFragment;
        if (SharedPrefsManager.sharedInstance(this).getDriverId() != null) {
            if (TripManager.sharedInstance(this).getTripManagerState().isUserOnDuty()) {
                firstFragment = new OnDutyFragment();
            }
            else {
                firstFragment = new OffDutyFragment();
            }
        }
        else {
            firstFragment = new LoginFragment();
        }
        replaceFragment(firstFragment);
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainContentView, newFragment);
        ft.commit();
    }
}
