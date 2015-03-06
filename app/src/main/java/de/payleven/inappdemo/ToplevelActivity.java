package de.payleven.inappdemo;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Top Level activity that allows displaying and dismissing a progress dialog
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
