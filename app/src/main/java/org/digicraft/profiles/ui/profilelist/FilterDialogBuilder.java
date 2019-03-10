package org.digicraft.profiles.ui.profilelist;

import android.content.Context;
import android.content.DialogInterface;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.FilterType;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Andrey Koryazhkin on 10-03-2019.
 */
class FilterDialogBuilder {

    AlertDialog build(Context context, int checkedItem, DialogInterface.OnClickListener listener) {

        final AtomicInteger valueKeeper = new AtomicInteger(checkedItem);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.filter_title));

        String[] items = FilterType.getStringArray(context);
        builder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> valueKeeper.set(which));

        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
            listener.onClick(dialog, valueKeeper.get());
        });
        builder.setNegativeButton(context.getString(R.string.cancel), null);

        return builder.create();

    }

}
