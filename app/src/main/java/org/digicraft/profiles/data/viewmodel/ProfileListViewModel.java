package org.digicraft.profiles.data.viewmodel;

import org.digicraft.profiles.data.model.Person;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileListViewModel extends ViewModel {

    private MutableLiveData<List<Person>> mProfileListLiveData = new MutableLiveData<>();

    public LiveData<List<Person>> getProfileListLiveData() {
        return mProfileListLiveData;
    }


    // TODO: 3/3/19 remove it
    public void fillDummyData() {

        List<Person> list = new ArrayList<>();
        list.add(new Person(
                2343, Person.GENDER_MALE, "John Smith", 38, null, "Golf, Fishing"));
        list.add(new Person(
                34343, Person.GENDER_FEMALE, "Hanna Johnson", 32, null, "Jogging"));

        mProfileListLiveData.postValue(list);
    }
}
