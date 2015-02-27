package de.payleven.inappdemo;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by android on 12/15/14.
 */
public class ToplevelActivity extends FragmentActivity {

    private ProgressDialogFragment progressDialogFragment;

    public void showProgressDialog() {
        final FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.addToBackStack("fragment_progress");

        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(fTransaction, "fragment_progress");
    }

    public void dismissProgressDialog() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
    }
}
