package org.digicraft.profiles.ui.profileadd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Person;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by Andrey Koryazhkin on 05-03-2019.
 */
public class ProfileAddFragment extends Fragment {


    private EditText mTxtName;
    private ProfileListViewModel mViewModel;
    private EditText mTxtAge;
    private EditText mTxtHobbies;
    private RadioButton mRbMale;
    private RadioButton mRbFemale;
    private Button mBtnSave;

    public static ProfileAddFragment newInstance() {
        Bundle args = new Bundle();
        ProfileAddFragment fragment = new ProfileAddFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_add_fragment, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTxtName = view.findViewById(R.id.txt_name);
        mTxtAge = view.findViewById(R.id.txt_age);
        mTxtHobbies = view.findViewById(R.id.txt_hobbies);
        mRbMale = view.findViewById(R.id.rb_male);
        mRbFemale = view.findViewById(R.id.rb_female);
        mBtnSave = view.findViewById(R.id.btn_save); // todo: make the menu instead of button
        mBtnSave.setOnClickListener(v -> onSavePressed());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTxtName.requestFocus();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(ProfileListViewModel.class);
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

    private void onBackPressed() {
        //noinspection ConstantConditions
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void onSavePressed() {
        if (checkFilled()) {
            String gender = mRbMale.isChecked() ? Person.GENDER_MALE : Person.GENDER_FEMALE;
            Integer age = Integer.valueOf(mTxtAge.getText().toString().trim());

            Person person = new Person(null,
                    mTxtName.getText().toString().trim(),
                    age, gender, null,
                    mTxtHobbies.getText().toString().trim()
            );
            showLoading();
            mViewModel.savePerson(person).observe(this, result -> {
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
    }

    private void showLoading() {
        //todo loading
    }

    private void hideLoading() {
        //todo loading
    }

    private boolean checkFilled() {
        if (mTxtName.getText().toString().trim().isEmpty()) {
            mTxtName.requestFocus();
            showMessage("Name is required field");
            return false;
        }
        if (mTxtAge.getText().toString().trim().isEmpty()) {
            mTxtAge.requestFocus();
            showMessage("Age is required field");
            return false;
        }

        try {
            Integer.valueOf(mTxtAge.getText().toString().trim());
        } catch (NumberFormatException e) {
            showMessage("Age must be a number");
            return false;
        }

        if (mRbMale.isChecked() == mRbFemale.isChecked()) {
            mRbMale.requestFocus();
            showMessage("Gender should be defined");
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
    }

}
