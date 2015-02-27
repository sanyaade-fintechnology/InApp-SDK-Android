package de.payleven.inappdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.payleven.inappsdk.SepaPaymentInstrument;
import de.payleven.inappsdk.errors.CallbackError;
import de.payleven.inappsdk.errors.ValidationError;
import de.payleven.inappsdk.errors.causes.ErrorCause;
import de.payleven.inappsdk.errors.causes.InvalidBIC;
import de.payleven.inappsdk.errors.causes.InvalidIBAN;
import de.payleven.inappsdk.listeners.AddPaymentInstrumentListener;

/**
 * View used for entering Sepa card details
 */
public class SepaActivity extends ToplevelActivity {

    private EditText ibanEditText;
    private EditText bicEditText;
    private TextView errorTextView;
    private EditText useCaseSelectionView;

    private PaylevenWrapper paylevenWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sepa);

        paylevenWrapper = PaylevenWrapper.getInstance();
        init();
    }

    private void init() {
        ibanEditText = (EditText) findViewById(R.id.iban_edittext);
        bicEditText = (EditText) findViewById(R.id.bic_edittext);
        errorTextView = (TextView) findViewById(R.id.error_textview);
        useCaseSelectionView = (EditText) findViewById(R.id.use_case_view);

        ibanEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String iban = ibanEditText.getText().toString();

                            if (!SepaPaymentInstrument.validateIban(iban).isValid()) {
                                ErrorCause errorCause = SepaPaymentInstrument.validateIban(iban)
                                        .getErrorCause();
                                ibanEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        bicEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String bic = bicEditText.getText().toString();

                            if (!SepaPaymentInstrument.validateBic(bic).isValid()) {
                                ErrorCause errorCause = SepaPaymentInstrument.validateBic(bic)
                                        .getErrorCause();
                                bicEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        Button addSepaPIButton = (Button) findViewById(R.id.add_sepa_button);
        addSepaPIButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPaymentInstrument();
                    }
                });
    }

    @NonNull
    private SepaPaymentInstrument getPaymentInstrument() {

        final String iban = ibanEditText.getText().toString();
        final String bic = bicEditText.getText().toString().isEmpty() ? 
                null : bicEditText.getText().toString();
        

        return new SepaPaymentInstrument(iban, bic);
    }


    private void addPaymentInstrument() {
        showProgressDialog();
        final SepaPaymentInstrument paymentInstrument = getPaymentInstrument();

        final String useCase = useCaseSelectionView.getText().toString();

        paylevenWrapper.addPaymentInstrument(
                paymentInstrument, useCase, new AddPaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentAddedSuccessfully(String userToken) {
                        dismissProgressDialog();
                        paylevenWrapper.saveUserToken(userToken);
                        Toast.makeText(
                                SepaActivity.this, R.string.sepa_added,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onPaymentInstrumentAddFailed(Throwable throwable) {
                        dismissProgressDialog();
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
                });
    }

    private void validateFields(@NonNull final ValidationError validationError) {
        for (ErrorCause errorCause : validationError.getErrorCauses()) {
            final String errorMessage = errorCause.getErrorMessage();
            if (errorCause instanceof InvalidIBAN) {
                ibanEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidBIC) {
                bicEditText.setError(errorMessage);
            }
        }
    }

}
