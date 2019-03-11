package org.digicraft.profiles.data.viewmodel;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import org.digicraft.profiles.data.db.ImageRemoteDataSource;
import org.digicraft.profiles.data.db.ProfileRemoteDataSource;
import org.digicraft.profiles.data.model.FilterType;
import org.digicraft.profiles.data.model.Profile;
import org.digicraft.profiles.data.model.SortType;
import org.digicraft.profiles.util.DataResponse;

import java.util.List;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static org.digicraft.profiles.util.Const.LOG_APP;

public class ProfileListViewModel extends ViewModel {

    private static final String LOG_TAG = LOG_APP + "ProfileListVM";
    private MediatorLiveData<List<Profile>> mProfileListLiveData = new MediatorLiveData<>();
    private LiveData<List<Profile>> mProfileListSourceLiveData = null;
    private HandlerThread mWorkerHandlerThread = null;
    private Handler mWorkerHandler = null;
    private MutableLiveData<Integer> mProfileIdLiveData = new MutableLiveData<>();
    private MediatorLiveData<Profile> mSingleProfileLiveData = null;
    private LiveData<Profile> mSingleProfileSourceLiveData = null;
    private ProfileRemoteDataSource mProfileRemoteDataSource;
    private int mFilterType = FilterType.getDefault();
    private int mSortType = SortType.getDefault();

    private Handler getWorkerHandler() {
        if (mWorkerHandler == null) {
            HandlerThread handlerThread = new HandlerThread("ProfileListVMHandlerThread");
            handlerThread.start();
            mWorkerHandler = new Handler(handlerThread.getLooper());
            mWorkerHandlerThread = handlerThread;
        }
        return mWorkerHandler;
    }

    private ProfileRemoteDataSource getProfileRemoteDataSource() {
        if (mProfileRemoteDataSource == null) {
            mProfileRemoteDataSource = new ProfileRemoteDataSource();
        }
        return mProfileRemoteDataSource;
    }

    public LiveData<List<Profile>> getProfileListLiveData() {
        return mProfileListLiveData;
    }

    public void refreshProfileList() {
        if (mProfileListSourceLiveData != null) {
            mProfileListLiveData.removeSource(mProfileListSourceLiveData);
            mProfileListSourceLiveData = null;
        }
        mProfileListSourceLiveData = getProfileRemoteDataSource().getData(mFilterType, mSortType);
        mProfileListLiveData.addSource(mProfileListSourceLiveData, profileList -> mProfileListLiveData.postValue(profileList));
    }

    public LiveData<DataResponse<Uri>> uploadImage(Uri localUri) {
        ImageRemoteDataSource ds = new ImageRemoteDataSource();
        return ds.saveNewImage(localUri);
    }

    public LiveData<Boolean> insertProfile(final Profile profile) {
        return getProfileRemoteDataSource().insertProfile(profile);
    }

    public LiveData<Boolean> updateProfile(final Profile profile) {
        return getProfileRemoteDataSource().updateProfile(profile);
    }

    public LiveData<Boolean> deleteProfile(Profile profile) {
        if (profile != null && profile.getFbId() != null && !profile.getFbId().isEmpty()) {
            return getProfileRemoteDataSource().deleteProfile(profile.getFbId());
        } else {
            throw new IllegalArgumentException("Incorrect profile id");
        }
    }

    @MainThread
    public void setCurrentProfileId(Integer profileId) {
        if (mSingleProfileLiveData != null) {
            mSingleProfileLiveData.setValue(null);
        }
        mProfileIdLiveData.setValue(profileId);
    }

    public MediatorLiveData<Profile> getSingleProfileLiveData() {
        if (mSingleProfileLiveData == null) {
            mSingleProfileLiveData = new MediatorLiveData<>();
            mSingleProfileLiveData.addSource(mProfileIdLiveData,
                    this::replaceSingleProfileLiveData);
        }
        return mSingleProfileLiveData;
    }

    private void replaceSingleProfileLiveData(Integer profileId) {
        if (mSingleProfileSourceLiveData != null) {
            mSingleProfileLiveData.removeSource(mSingleProfileSourceLiveData);
            mSingleProfileSourceLiveData = null;
        }
        if (profileId != null) {
            mSingleProfileSourceLiveData = getProfileRemoteDataSource().getProfile(profileId);
            mSingleProfileLiveData.addSource(mSingleProfileSourceLiveData, profile -> mSingleProfileLiveData.postValue(profile));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mWorkerHandlerThread != null && mWorkerHandlerThread.isAlive()) {
            mWorkerHandlerThread.quit();
        }
    }

    public int getFilterType() {
        return mFilterType;
    }

    public void setFilterType(int filterType) {
        if (mFilterType != filterType) {
            mFilterType = filterType;
            refreshProfileList();
        }
    }

    public int getSortType() {
        return mSortType;
    }

    public void setSortType(int sortType) {
        if (mSortType != sortType) {
            mSortType = sortType;
            refreshProfileList();
        }
    }
}
