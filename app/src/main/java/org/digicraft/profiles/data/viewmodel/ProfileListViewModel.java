package org.digicraft.profiles.data.viewmodel;

import android.os.Handler;
import android.os.HandlerThread;

import org.digicraft.profiles.data.db.ProfileRemoteDataSource;
import org.digicraft.profiles.data.model.Person;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static org.digicraft.profiles.util.Const.LOG_APP;

public class ProfileListViewModel extends ViewModel {

    private static final String LOG_TAG = LOG_APP + "ProfileListVM";
    private MediatorLiveData<List<Person>> mProfileListLiveData = new MediatorLiveData<>();
    private LiveData<List<Person>> mProfileListSourceLiveData = null;
    private HandlerThread mWorkerHandlerThread = null;
    private Handler mWorkerHandler = null;
    private MutableLiveData<Integer> mPersonIdLiveData = new MutableLiveData<>();
    private MediatorLiveData<Person> mSinglePersonLiveData = null;
    private LiveData<Person> mSinglePersonSourceLiveData = null;
    private ProfileRemoteDataSource mProfileRemoteDataSource;

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

    public LiveData<List<Person>> getProfileListLiveData() {
        return mProfileListLiveData;
    }

    public void refreshProfileList() {
        if (mProfileListSourceLiveData != null) {
            mProfileListLiveData.removeSource(mProfileListSourceLiveData);
            mProfileListSourceLiveData = null;
        }
        mProfileListSourceLiveData = getProfileRemoteDataSource().getData();
        mProfileListLiveData.addSource(mProfileListSourceLiveData, personList -> mProfileListLiveData.postValue(personList));
    }

//    public LiveData<Boolean> savePerson(final Person person) {
//        final MutableLiveData<Boolean> result = new MutableLiveData<>();
//        getWorkerHandler().post(() -> {
//            try {
//                /// save data here
//
//                List<Person> list = mProfileListLiveData.getValue();
//                if (list == null) {
//                    list = new ArrayList<>();
//                }
//
//                // in the real app we should check id to choose to update or insert
//                if (list.indexOf(person) == -1) {
//                    list.add(person);
//                }
//                mProfileListLiveData.postValue(list);
//                result.postValue(Boolean.TRUE);
//            } catch (Exception e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                result.postValue(Boolean.FALSE);
//            }
//        });
//        return result;
//    }

    public LiveData<Boolean> savePerson(final Person person) {
        if (person.getFbId() == null || person.getFbId().isEmpty()) {
            return getProfileRemoteDataSource().insertPerson(person);
        } else {
            return getProfileRemoteDataSource().updatePerson(person);
        }
    }

    public LiveData<Boolean> deletePerson(Person person) {
        if (person != null && person.getFbId() != null && !person.getFbId().isEmpty()) {
            return getProfileRemoteDataSource().deletePerson(person.getFbId());
        } else {
            throw new IllegalArgumentException("Incorrect person id");
        }
    }

    public void setCurrentPersonId(Integer personId) {
        mPersonIdLiveData.postValue(personId);
    }

    public MediatorLiveData<Person> getSinglePersonLiveData() {
        if (mSinglePersonLiveData == null) {
            mSinglePersonLiveData = new MediatorLiveData<>();
//            mSinglePersonLiveData.addSource(getProfileListLiveData(),
//                    personList -> updateSinglePersonLiveData(personList, mPersonIdLiveData.getValue()));
            mSinglePersonLiveData.addSource(mPersonIdLiveData,
                    this::replaceSinglePersonLiveData);
        }
        return mSinglePersonLiveData;
    }

    private void replaceSinglePersonLiveData(Integer personId) {
        if (mSinglePersonSourceLiveData != null) {
            mSinglePersonLiveData.removeSource(mSinglePersonSourceLiveData);
            mSinglePersonSourceLiveData = null;
        }
        if (personId != null) {
            mSinglePersonSourceLiveData = getProfileRemoteDataSource().getPerson(personId);
            mSinglePersonLiveData.addSource(mSinglePersonSourceLiveData, person -> mSinglePersonLiveData.postValue(person));
        }
    }

    // TODO: 3/3/19 remove it

    public void fillDummyData() {

        List<Person> list = new ArrayList<>();
        list.add(new Person(
                2343, "John Smith", 38, Person.GENDER_MALE, null, "Golf, Fishing"));
        list.add(new Person(
                34343, "Hanna Johnson", 32, Person.GENDER_FEMALE, null, "Jogging"));

        mProfileListLiveData.postValue(list);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mWorkerHandlerThread.quit();
    }

}
