package org.digicraft.profiles.data.db;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.digicraft.profiles.data.model.Person;
import org.digicraft.profiles.util.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Andrey Koryazhkin on 08-03-2019.
 */
public class ProfileRemoteDataSource {

    private static final String LOG_TAG = Const.LOG_APP + "ProfileRemoteDS";
    private static final String DB_PROFILES = "ps_profiles";

    public MediatorLiveData<List<Person>> getData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(DB_PROFILES);

        MediatorLiveData<List<Person>> result = new MediatorLiveData<>();
        result.addSource(new FirebaseQueryLiveData(query), documentSnapshots -> {
            if (documentSnapshots != null) {
                List<Person> list = new ArrayList<>();
                for (DocumentSnapshot doc : documentSnapshots) {
                    try {
                        Person person = doc.toObject(Person.class);
                        list.add(person);
                    } catch (Exception e) {
                        // ignore invalid data
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
                result.setValue(list);
            }
        });
        return result;

    }

    public LiveData<Person> getPerson(Integer personId) {
        MediatorLiveData<Person> result = new MediatorLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(DB_PROFILES).whereEqualTo(Person.F_ID, personId);

        result.addSource(new FirebaseQueryLiveData(query), documentSnapshots -> {
            if (documentSnapshots != null) {
                for (DocumentSnapshot doc : documentSnapshots) {
                    try {
                        Person person = doc.toObject(Person.class);
                        //noinspection ConstantConditions
                        person.setFbId(doc.getId());
                        result.postValue(person);
                    } catch (Exception e) {
                        // ignore invalid data
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        });

        return result;

    }

    public LiveData<Boolean> insertPerson(Person person) {

        MutableLiveData<Boolean> result = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (person.getId() == null) {
            // todo: there can be collision here. need to do better id generation
            person.setId(getRandomId());
        }

        db.collection(DB_PROFILES)
                .add(person)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    person.setFbId(id);
                    Log.d(LOG_TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    result.postValue(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error while adding document", e);
                    result.postValue(Boolean.FALSE);
                });

        return result;

    }

    public LiveData<Boolean> updatePerson(Person person) {

        MutableLiveData<Boolean> result = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(DB_PROFILES).document(person.getFbId())
                .set(person)
                .addOnSuccessListener(documentReference -> result.postValue(Boolean.TRUE))
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error while updating document", e);
                    result.postValue(Boolean.FALSE);
                });

        return result;

    }

    private int getRandomId() {

        return Math.abs(UUID.randomUUID().hashCode());

    }

    public LiveData<Boolean> deletePerson(String fbId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DB_PROFILES).document(fbId).delete()
                .addOnSuccessListener(documentReference -> result.postValue(Boolean.TRUE))
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error while deleting the document", e);
                    result.postValue(Boolean.FALSE);
                });
        return result;
    }
}
