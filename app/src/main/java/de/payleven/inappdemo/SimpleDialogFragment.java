package de.payleven.inappdemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Base class which helps to build alert and confirmation dialogs utilizing
 * {@link android.support.v4.app.DialogFragment}
 */
public class SimpleDialogFragment extends DialogFragment {

    public static final String TAG = SimpleDialogFragment.class.getSimpleName();

    private static final String MESSAGE = "message";
    private static final String TITLE = "title";


    public static SimpleDialogFragment newInstance(String title, String message) {
        final SimpleDialogFragment fragment = new SimpleDialogFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);

        fragment.setArguments(bundle);
        fragment.setCancelable(false);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString(TITLE);
        final String message = getArguments().getString(MESSAGE);

        String positiveButtonResource = getString(android.R.string.ok);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton(positiveButtonResource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        return builder.create();
    }
}
