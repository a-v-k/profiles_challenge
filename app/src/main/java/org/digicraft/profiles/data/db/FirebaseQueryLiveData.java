package org.digicraft.profiles.data.db;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import androidx.lifecycle.LiveData;


/**
 * Created by Andrey Koryazhkin on 08-03-2019.
 */

public class FirebaseQueryLiveData extends LiveData<List<DocumentSnapshot>> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query mQuery;
    private ListenerRegistration mListenerRegistration = null;

    public FirebaseQueryLiveData(Query query) {
        this.mQuery = query;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        mListenerRegistration = mQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } else if (queryDocumentSnapshots != null) {
                postValue(queryDocumentSnapshots.getDocuments());
            }
        });
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        if (mListenerRegistration != null) {
            mListenerRegistration.remove();
            mListenerRegistration = null;
        }
    }

}