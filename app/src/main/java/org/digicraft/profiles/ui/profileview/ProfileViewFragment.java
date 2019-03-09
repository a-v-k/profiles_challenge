package org.digicraft.profiles.ui.profileview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Profile;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by Andrey Koryazhkin on 07-03-2019.
 */
public class ProfileViewFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "arg_profile_id";
    private Integer mProfileId = null;
    private Profile mProfile = null;
    private ProfileListViewModel mViewModel;
    private TextView mTxtName;
    private TextView mTxtAge;
    private EditText mTxtHobbies;
    private TextView mTxtGender;
    private Button mBtnSave;

    public static ProfileViewFragment newInstance(Integer profileId) {
        ProfileViewFragment profileViewFragment = new ProfileViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PROFILE_ID, profileId);
        profileViewFragment.setArguments(args);
        return profileViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mProfileId = getArguments().getInt(ARG_PROFILE_ID, Integer.MIN_VALUE);
        }
        if (mProfileId == null || mProfileId == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("ARG_PROFILE_ID should be defined");
        }
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ProfileListViewModel.class);
        mViewModel.setCurrentProfileId(mProfileId);
        mViewModel.getSingleProfileLiveData().observe(this, profile -> {
            mProfile = profile;
            fillProfile(profile);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_view_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTxtName = view.findViewById(R.id.txt_name);
        mTxtAge = view.findViewById(R.id.txt_age);
        mTxtHobbies = view.findViewById(R.id.txt_hobbies);
        mTxtGender = view.findViewById(R.id.txt_gender);
        mBtnSave = view.findViewById(R.id.btn_save); // todo: make the menu instead of button
        mBtnSave.setOnClickListener(v -> saveProfile());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                deleteProfile();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void fillProfile(Profile profile) {
        // TODO: 3/7/19 show image
        mTxtName.setText(profile.getName());
        mTxtAge.setText(String.valueOf(profile.getAge()));
        mTxtGender.setText(profile.getGender());
        mTxtHobbies.setText(profile.getHobbies());
    }

    private void saveProfile() {
        mProfile.setHobbies(mTxtHobbies.getText().toString());
        showLoading();
        mViewModel.saveProfile(mProfile).observe(this, result -> {
            if (result != null) {
                hideLoading();
                if (result) {
                    onBackPressed();
                }
            } else {
                showMessage("Error"); // todo: make good message
            }
        });

    }

    private void deleteProfile() {
        // TODO: 3/9/19 show confirmation dialog
        if (mProfile != null && mProfile.getFbId() != null && !mProfile.getFbId().isEmpty()) {
            showLoading();
            mViewModel.deleteProfile(mProfile).observe(this, result -> {
                if (result != null) {
                    if (result) {
                        hideLoading();
                        onBackPressed();
                    } else {
                        hideLoading();
                        showMessage("Error while deleting profile");
                    }
                }
            });
        }
    }

    private void showLoading() {
        //todo loading
    }

    private void hideLoading() {
        //todo loading
    }

    private void onBackPressed() {
        //noinspection ConstantConditions
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void showMessage(String message) {
        // TODO: 3/7/19 make snackbar
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
    }

}
