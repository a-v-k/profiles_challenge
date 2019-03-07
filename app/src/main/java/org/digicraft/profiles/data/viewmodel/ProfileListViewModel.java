package org.digicraft.profiles.data.viewmodel;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.digicraft.profiles.data.model.Person;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static org.digicraft.profiles.util.Const.LOG_APP;

public class ProfileListViewModel extends ViewModel {

    private static final String LOG_TAG = LOG_APP + "ProfileListVM";
    private MutableLiveData<List<Person>> mProfileListLiveData = new MutableLiveData<>();
    private HandlerThread mWorkerHandlerThread = null;
    private Handler mWorkerHandler = null;
    //private

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
                list.add(person);
                mProfileListLiveData.postValue(list);
                result.postValue(Boolean.TRUE);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                result.postValue(Boolean.FALSE);
            }
        });
        return result;
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
