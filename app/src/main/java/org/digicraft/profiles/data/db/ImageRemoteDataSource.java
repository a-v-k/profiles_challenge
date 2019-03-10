package org.digicraft.profiles.data.db;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.digicraft.profiles.util.DataResponse;

import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Andrey Koryazhkin on 09-03-2019.
 */
public class ImageRemoteDataSource {

    private static final String STR_BUCKET = "gs://passportprofiles.appspot.com";
    private static final String STR_IMAGE_SET = "ps_profile_images";


    public LiveData<DataResponse<Uri>> saveNewImage(Uri localUri) {

        MutableLiveData<DataResponse<Uri>> result = new MutableLiveData<>();
        FirebaseStorage storage = FirebaseStorage.getInstance(STR_BUCKET);

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(STR_IMAGE_SET);
        // assume that we are always have a jpg
        StorageReference spaceRef = imagesRef.child(makeImageId() + ".jpg");

        UploadTask uploadTask = spaceRef.putFile(localUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                result.postValue(getFailureResponse(task));
            }

            // Continue with the task to get the download URL
            return spaceRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Uri downloadUri = task.getResult();
                result.postValue(new DataResponse<>(downloadUri));
            } else {
                result.postValue(getFailureResponse(task));
            }
        });

        return result;

    }

    private <T> DataResponse<T> getFailureResponse(Task failedTask) {
        if (failedTask.getException() != null) {
            return new DataResponse<>(failedTask.getException());
        } else {
            return new DataResponse<>(new IllegalStateException("unknown result"));
        }
    }

    private String makeImageId() {
        // todo: make better name generator
        return UUID.randomUUID().toString();

    }

}
