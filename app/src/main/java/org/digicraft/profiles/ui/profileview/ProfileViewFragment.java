package org.digicraft.profiles.ui.profileview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Profile;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;
import org.digicraft.profiles.ui.base.BaseFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by Andrey Koryazhkin on 07-03-2019.
 */
public class ProfileViewFragment extends BaseFragment {

    private static final String ARG_PROFILE_ID = "arg_profile_id";
    private Integer mProfileId = null;
    private Profile mProfile = null;
    private ProfileListViewModel mViewModel;
    private TextView mTxtName;
    private TextView mTxtAge;
    private EditText mTxtHobbies;
    private TextView mTxtGender;
    @SuppressWarnings("FieldCanBeLocal")
    private Button mBtnSave;
    private ImageView mImgPerson;
    private TextView mTvImage;

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
            if (profile != null) {
                mProfile = profile;
                fillProfile(profile);
            }
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
        toolbar.setTitle(R.string.title_profile_view);
        mImgPerson = view.findViewById(R.id.img_person);
        mTvImage = view.findViewById(R.id.tv_image_hint);
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
                deleteProfileDialog();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void fillProfile(Profile profile) {
        if (profile.getImage() != null && !profile.getImage().isEmpty()) {
            Glide.with(this).load(profile.getImage()).centerCrop().into(mImgPerson);
            mTvImage.setVisibility(View.GONE);
        }
        mTxtName.setText(profile.getName());
        mTxtAge.setText(makeAgeString(profile));
        mTxtGender.setText(makeGenderString(profile));
        mTxtHobbies.setText(profile.getHobbies());
    }

    private String makeAgeString(Profile profile) {
        return Objects.requireNonNull(getContext()).getString(R.string.age_dots, profile.getAge());
    }

    private String makeGenderString(Profile profile) {
        Context context = Objects.requireNonNull(getContext());
        String gender;
        if (Profile.GENDER_MALE.equals(profile.getGender())) {
            gender = context.getString(R.string.male);
        } else {
            gender = context.getString(R.string.female);
        }
        return context.getString(R.string.gender_dots, gender);
    }

    private void saveProfile() {
        mProfile.setHobbies(mTxtHobbies.getText().toString());
        showLoading();
        mViewModel.updateProfile(mProfile).observe(this, result -> {
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

    private void deleteProfileDialog() {
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(R.string.delete_confirmation)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteProfile())
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss()).show();
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

    private void onBackPressed() {
        //noinspection ConstantConditions
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


}
