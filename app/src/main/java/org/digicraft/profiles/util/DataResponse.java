package org.digicraft.profiles.util;

import androidx.annotation.NonNull;

/**
 * Created by Andrey Koryazhkin on 10-04-2017.
 */
public class DataResponse<T> {

    private T mData;
    private Exception mException;

    public DataResponse(@NonNull T data) {
        mData = data;
        //noinspection ConstantConditions
        if (data == null) {
            throw new IllegalArgumentException("data must be not null");
        }
    }

    public DataResponse(@NonNull Exception e) {
        this.mException = e;
    }

    public T getData() {
        return mData;
    }

    public Exception getException() {
        return mException;
    }

    public boolean isSuccessful() {
        return mData != null;
    }

}
