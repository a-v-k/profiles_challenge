package org.digicraft.profiles.data.model;

import android.content.Context;

import org.digicraft.profiles.R;

/**
 * Created by Andrey Koryazhkin on 10-03-2019.
 */
public class FilterType {

    public static final int FILTER_MALE = 0;
    public static final int FILTER_FEMALE = 1;
    public static final int FILTER_ALL = 2;

    public static String[] getStringArray(Context context) {
        String[] res = new String[3];
        res[0] = context.getString(R.string.male);
        res[1] = context.getString(R.string.female);
        res[2] = context.getString(R.string.all);
        return res;
    }

    public static int getDefault() {
        return FILTER_ALL;
    }

}
