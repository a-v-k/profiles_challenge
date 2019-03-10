package org.digicraft.profiles.ui.base;

import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * Created by Andrey Koryazhkin on 10-03-2019.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog = null;


    protected void showLoading() {
        // todo: use another progress indicator
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.show();
        }
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    protected void showMessage(String message) {
        // TODO: 3/7/19 make snackbar
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
    }


}
