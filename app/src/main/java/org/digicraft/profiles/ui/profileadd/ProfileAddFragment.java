package org.digicraft.profiles.ui.profileadd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Profile;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;
import org.digicraft.profiles.ui.base.BaseFragment;
import org.digicraft.profiles.util.Const;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Andrey Koryazhkin on 05-03-2019.
 */
public class ProfileAddFragment extends BaseFragment {

    private static final String LOG_TAG = Const.LOG_APP + "ProfileAddFr";
    private static final int PICK_IMAGE_CODE = 22;
    private ImageView mImgPerson;
    private Uri mImageUri = null;
    private EditText mTxtName;
    private ProfileListViewModel mViewModel;
    private EditText mTxtAge;
    private EditText mTxtHobbies;
    private RadioButton mRbMale;
    private RadioButton mRbFemale;
    private Button mBtnSave;
    private TextView mTvImage;

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
        mImgPerson = view.findViewById(R.id.img_person);
        mTvImage = view.findViewById(R.id.tv_image_hint);
        mTxtName = view.findViewById(R.id.txt_name);
        mTxtAge = view.findViewById(R.id.txt_age);
        mTxtHobbies = view.findViewById(R.id.txt_hobbies);
        mRbMale = view.findViewById(R.id.rb_male);
        mRbFemale = view.findViewById(R.id.rb_female);
        mBtnSave = view.findViewById(R.id.btn_save); // todo: make the menu instead of button
        mBtnSave.setOnClickListener(this::onSavePressed);
        mImgPerson.setOnClickListener(this::onImageClicked);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTxtName.requestFocus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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

    private void onImageClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                // this is the image selected by the user
                Uri imageUri = resultData.getData();
                Log.d(LOG_TAG, String.valueOf(imageUri));
                if (imageUri != null) {
                    //showImage(imageUri);
                    uploadAndShowImage(imageUri);
                }
            }
        }
    }

    private void uploadAndShowImage(Uri imageUri) {
        showLoading();
        mViewModel.uploadImage(imageUri).observe(this, uriDataResponse -> {
            if (uriDataResponse != null) {
                hideLoading();
                if (uriDataResponse.isSuccessful()) {
                    showImage(uriDataResponse.getData());
                } else {
                    showMessage(uriDataResponse.getException().getMessage());
                }
            }
        });
    }

    private void showImage(Uri imageUri) {
        // TODO: 3/9/19 save imageUrl in onSaveInstanceState
        // todo: show loading image as image placeholder
        Glide.with(this).load(imageUri).centerCrop().into(mImgPerson);
        mImageUri = imageUri;
        mTvImage.setVisibility(View.GONE);
    }

    private void onSavePressed(View view) {
        if (checkFilled()) {
            String gender = mRbMale.isChecked() ? Profile.GENDER_MALE : Profile.GENDER_FEMALE;
            Integer age = Integer.valueOf(mTxtAge.getText().toString().trim());

            Profile profile = new Profile(null,
                    mTxtName.getText().toString().trim(),
                    age, gender, mImageUri != null ? mImageUri.toString() : null,
                    mTxtHobbies.getText().toString().trim()
            );
            showLoading();

            mViewModel.insertProfile(profile).observe(this, result -> {
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


}
