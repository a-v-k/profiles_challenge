package org.digicraft.profiles.data.model;

import android.content.Context;

import org.digicraft.profiles.R;

/**
 * Created by Andrey Koryazhkin on 10-03-2019.
 */
public class SortType {

    public static final int SORT_NAME_ASC = 0;
    public static final int SORT_NAME_DESC = 1;
    public static final int SORT_AGE_ASC = 2;
    public static final int SORT_AGE_DESC = 3;
    public static final int SORT_NONE = 4;

    public static String[] getStringArray(Context context) {
        String[] res = new String[5];
        res[0] = context.getString(R.string.sort_name_asc);
        res[1] = context.getString(R.string.sort_name_desc);
        res[2] = context.getString(R.string.sort_age_asc);
        res[3] = context.getString(R.string.sort_age_desc);
        res[4] = context.getString(R.string.sort_none);
        return res;
    }

    public static int getDefault() {
        return SORT_NONE;
    }


}
