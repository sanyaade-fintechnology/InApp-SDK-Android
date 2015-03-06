package de.payleven.inappdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.payleven.inappsdk.PayPalPaymentInstrument;
import de.payleven.inappsdk.errors.CallbackError;
import de.payleven.inappsdk.errors.ValidationError;
import de.payleven.inappsdk.errors.causes.ErrorCause;
import de.payleven.inappsdk.errors.causes.InvalidPayPalAuthToken;
import de.payleven.inappsdk.listeners.AddPaymentInstrumentListener;

/**
 * Activity used for entering PayPal payment instrument details and requesting adding of the 
 * payment instrument  
 */
public class PayPalActivity extends FragmentActivity {

    private EditText authTokenEditText;
    private TextView errorTextView;
    private EditText useCaseSelectionView;
    private ProgressDialogFragment progressDialogFragment;


    private PaylevenWrapper paylevenWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        paylevenWrapper = PaylevenWrapper.getInstance();
        init();
    }

    private void init() {
        authTokenEditText = (EditText) findViewById(R.id.paypal_auth_edittext);
        errorTextView = (TextView) findViewById(R.id.error_textview);
        useCaseSelectionView = (EditText) findViewById(R.id.use_case_view);

        authTokenEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String authToken = authTokenEditText.getText().toString();
                            if (!PayPalPaymentInstrument.validateAuthToken(authToken)
                                    .isValid()) {
                                ErrorCause errorCause = PayPalPaymentInstrument.validateAuthToken(
                                        authToken).getErrorCause();
                                authTokenEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        Button addSepaPIButton = (Button) findViewById(R.id.add_paypal_button);
        addSepaPIButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPaymentInstrument();
                    }
                });
    }

    @Nullable
    private PayPalPaymentInstrument getPaymentInstrument() {

        final String authToken = authTokenEditText.getText().toString();

        return new PayPalPaymentInstrument(authToken);
    }

    private void showProgressDialog() {
        final FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.addToBackStack("fragment_progress");

        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(fTransaction, "fragment_progress");
    }

    private void dismissProgressDialog() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
    }

    /**
     * Request the {@link de.payleven.inappdemo.PaylevenWrapper} to add a payment instrument.
     * Displays a progress dialog while the action is being done. 
     * Handles success and failure cases.
     */
    private void addPaymentInstrument() {
        showProgressDialog();
        final PayPalPaymentInstrument paymentInstrument = getPaymentInstrument();
        final String useCase = useCaseSelectionView.getText().toString();

        paylevenWrapper.addPaymentInstrument(
                paymentInstrument, useCase, new AddPaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentAddedSuccessfully(String userToken) {
                        dismissProgressDialog();
                        paylevenWrapper.saveUserToken(userToken);
                        Toast.makeText(
                                PayPalActivity.this, R.string.paypal_added,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onPaymentInstrumentAddFailed(Throwable throwable) {
                        dismissProgressDialog();
                        // display the reason why this failed.
                        if (throwable instanceof CallbackError) {
                            if (throwable instanceof ValidationError) {
                                String error = Util.getErrorFormatted((ValidationError) throwable);
                                errorTextView.setText(error);
                                validateFields((ValidationError) throwable);
                            } else {
                                errorTextView.setText(
                                        throwable.toString() + " " + ((CallbackError) throwable)
                                                .getErrorCode());
                            }
                        }
                    }
                });
    }

    private void validateFields(@NonNull final ValidationError validationError) {
        for (ErrorCause errorCause : validationError.getErrorCauses()) {
            final String errorMessage = errorCause.getErrorMessage();
            if (errorCause instanceof InvalidPayPalAuthToken) {
                authTokenEditText.setError(errorMessage);
            }
        }
    }
}
