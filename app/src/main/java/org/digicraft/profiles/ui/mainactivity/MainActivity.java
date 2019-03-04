package org.digicraft.profiles.ui.mainactivity;

import android.os.Bundle;

import org.digicraft.profiles.R;
import org.digicraft.profiles.ui.profilelist.ProfileListFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction tran = fm.beginTransaction();
            tran.replace(R.id.content, ProfileListFragment.newInstance());
            tran.commit();
        }
    }


}
