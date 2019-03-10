package org.digicraft.profiles.data.db;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.digicraft.profiles.data.model.Profile;
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

    public MediatorLiveData<List<Profile>> getData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(DB_PROFILES);

        MediatorLiveData<List<Profile>> result = new MediatorLiveData<>();
        result.addSource(new FirebaseQueryLiveData(query), documentSnapshots -> {
            if (documentSnapshots != null) {
                List<Profile> list = new ArrayList<>();
                for (DocumentSnapshot doc : documentSnapshots) {
                    try {
                        Profile profile = doc.toObject(Profile.class);
                        list.add(profile);
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

    public LiveData<Profile> getProfile(Integer profileId) {
        MediatorLiveData<Profile> result = new MediatorLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection(DB_PROFILES).whereEqualTo(Profile.F_ID, profileId);

        result.addSource(new FirebaseQueryLiveData(query), documentSnapshots -> {
            if (documentSnapshots != null) {
                for (DocumentSnapshot doc : documentSnapshots) {
                    try {
                        Profile profile = doc.toObject(Profile.class);
                        //noinspection ConstantConditions
                        profile.setFbId(doc.getId());
                        result.postValue(profile);
                    } catch (Exception e) {
                        // ignore invalid data
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        });

        return result;

    }

    public LiveData<Boolean> insertProfile(Profile profile) {

        MutableLiveData<Boolean> result = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (profile.getId() == null) {
            // todo: there can be collision here. need to do better id generation
            profile.setId(getRandomId());
        }

        db.collection(DB_PROFILES)
                .add(profile)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    profile.setFbId(id);
                    Log.d(LOG_TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    //updateProfile(profile);
                    result.postValue(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error while adding document", e);
                    result.postValue(Boolean.FALSE);
                });

        return result;

    }

    public LiveData<Boolean> updateProfile(Profile profile) {

        MutableLiveData<Boolean> result = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(DB_PROFILES).document(profile.getFbId())
                .set(profile)
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

    public LiveData<Boolean> deleteProfile(String fbId) {
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
