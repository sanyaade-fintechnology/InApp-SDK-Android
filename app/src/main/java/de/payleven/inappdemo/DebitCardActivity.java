package de.payleven.inappdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.payleven.inappsdk.DebitCardPaymentInstrument;
import de.payleven.inappsdk.errors.CallbackError;
import de.payleven.inappsdk.errors.ValidationError;
import de.payleven.inappsdk.errors.causes.ErrorCause;
import de.payleven.inappsdk.errors.causes.InvalidAccountNumber;
import de.payleven.inappsdk.errors.causes.InvalidRoutingNumber;
import de.payleven.inappsdk.listeners.AddPaymentInstrumentListener;

/**
 * Activity used for entering credit card details.
 */
public class DebitCardActivity extends ToplevelActivity {

    private EditText accountNoEditText;
    private EditText routingNoEditText;
    private TextView errorTextView;
    private EditText useCaseSelectionView;
    private ProgressDialogFragment progressDialogFragment;


    private PaylevenWrapper paylevenWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_card);

        paylevenWrapper = PaylevenWrapper.getInstance();
        init();
    }

    private void init() {
        accountNoEditText = (EditText) findViewById(R.id.account_number_edittext);
        routingNoEditText = (EditText) findViewById(R.id.routing_number_edittext);
        errorTextView = (TextView) findViewById(R.id.error_textview);
        useCaseSelectionView = (EditText) findViewById(R.id.use_case_view);

        accountNoEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String accountNumber = accountNoEditText.getText().toString();
                            if (!DebitCardPaymentInstrument.validateAccountNumber(accountNumber)
                                    .isValid()) {
                                ErrorCause errorCause = DebitCardPaymentInstrument
                                        .validateAccountNumber(accountNumber).getErrorCause();
                                accountNoEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        routingNoEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String routingNumber = routingNoEditText.getText().toString();
                            if (!DebitCardPaymentInstrument.validateRoutingNumber(routingNumber)
                                    .isValid()) {
                                ErrorCause errorCause = DebitCardPaymentInstrument
                                        .validateRoutingNumber(routingNumber).getErrorCause();
                                routingNoEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });


        Button addDebitCardPIButton = (Button) findViewById(R.id.add_debit_button);
        addDebitCardPIButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPaymentInstrument();
                    }
                });
    }

    @Nullable
    private DebitCardPaymentInstrument getPaymentInstrument() {
        final String accountNumber = accountNoEditText.getText().toString();
        final String routingNumber = routingNoEditText.getText().toString();

        return new DebitCardPaymentInstrument(accountNumber, routingNumber);

    }

    private void addPaymentInstrument() {
        showProgressDialog();
        final DebitCardPaymentInstrument paymentInstrument = getPaymentInstrument();
        final String useCase = useCaseSelectionView.getText().toString();

        paylevenWrapper.addPaymentInstrument(
                paymentInstrument, useCase, new AddPaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentAddedSuccessfully(String userToken) {
                        dismissProgressDialog();
                        paylevenWrapper.saveUserToken(userToken);
                        Toast.makeText(
                                DebitCardActivity.this, R.string.dd_added,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onPaymentInstrumentAddFailed(Throwable throwable) {
                        dismissProgressDialog();
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
            if (errorCause instanceof InvalidAccountNumber) {
                accountNoEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidRoutingNumber) {
                routingNoEditText.setError(errorMessage);
            }
        }
    }
}
