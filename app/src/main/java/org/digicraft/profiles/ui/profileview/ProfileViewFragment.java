package org.digicraft.profiles.ui.profileview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Person;
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

    private static final String ARG_PERSON_ID = "arg_person_id";
    private Integer mPersonId = null;
    private Person mPerson = null;
    private ProfileListViewModel mViewModel;
    private TextView mTxtName;
    private TextView mTxtAge;
    private EditText mTxtHobbies;
    private TextView mTxtGender;
    private Button mBtnSave;

    public static ProfileViewFragment newInstance(Integer personId) {
        ProfileViewFragment profileViewFragment = new ProfileViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PERSON_ID, personId);
        profileViewFragment.setArguments(args);
        return profileViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mPersonId = getArguments().getInt(ARG_PERSON_ID, Integer.MIN_VALUE);
        }
        if (mPersonId == null || mPersonId == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("ARG_PERSON_ID should be defined");
        }
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ProfileListViewModel.class);
        mViewModel.setCurrentPersonId(mPersonId);
        mViewModel.getSinglePersonLiveData().observe(this, person -> {
            mPerson = person;
            fillProfile(person);
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
        mBtnSave.setOnClickListener(v -> onSavePressed());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }


    private void fillProfile(Person person) {
        // TODO: 3/7/19 show image
        mTxtName.setText(person.getName());
        mTxtAge.setText(String.valueOf(person.getAge()));
        mTxtGender.setText(person.getGender());
        mTxtHobbies.setText(person.getHobbies());
    }

    private void onSavePressed() {
        mPerson.setHobbies(mTxtHobbies.getText().toString());
        showLoading();
        mViewModel.savePerson(mPerson).observe(this, result -> {
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
