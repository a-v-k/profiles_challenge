package org.digicraft.profiles.data.viewmodel;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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
    private MutableLiveData<List<Person>> mProfileListLiveData = new MutableLiveData<>();
    private HandlerThread mWorkerHandlerThread = null;
    private Handler mWorkerHandler = null;
    private MutableLiveData<Integer> mPersonIdLiveData = new MutableLiveData<>();
    private MediatorLiveData<Person> mSinglePersonLiveData = null;

    private Handler getWorkerHandler() {
        if (mWorkerHandler == null) {
            HandlerThread handlerThread = new HandlerThread("ProfileListVMHandlerThread");
            handlerThread.start();
            mWorkerHandler = new Handler(handlerThread.getLooper());
            mWorkerHandlerThread = handlerThread;
        }
        return mWorkerHandler;
    }


    public LiveData<List<Person>> getProfileListLiveData() {
        return mProfileListLiveData;
    }

    public LiveData<Boolean> savePerson(final Person person) {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        getWorkerHandler().post(() -> {
            try {
                /// save data here

                List<Person> list = mProfileListLiveData.getValue();
                if (list == null) {
                    list = new ArrayList<>();
                }

                // in the real app we should check id to choose to update or insert
                if (list.indexOf(person) == -1) {
                    list.add(person);
                }
                mProfileListLiveData.postValue(list);
                result.postValue(Boolean.TRUE);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                result.postValue(Boolean.FALSE);
            }
        });
        return result;
    }

    public void setCurrentPersonId(Integer personId) {
        mPersonIdLiveData.postValue(personId);
    }

    public MediatorLiveData<Person> getSinglePersonLiveData() {
        if (mSinglePersonLiveData == null) {
            mSinglePersonLiveData = new MediatorLiveData<>();
            mSinglePersonLiveData.addSource(getProfileListLiveData(),
                    personList -> updateSinglePersonLiveData(personList, mPersonIdLiveData.getValue()));
            mSinglePersonLiveData.addSource(mPersonIdLiveData,
                    personId -> updateSinglePersonLiveData(getProfileListLiveData().getValue(), personId));
        }
        return mSinglePersonLiveData;
    }

    private void updateSinglePersonLiveData(List<Person> personList, Integer personId) {
        if (personId != null && personList != null) {
            for (Person person : personList) {
                if (personId.equals(person.getId())) {
                    mSinglePersonLiveData.postValue(person);
                    return;
                }
                mSinglePersonLiveData.postValue(null);
            }
        } else {
            mSinglePersonLiveData.postValue(null);
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
