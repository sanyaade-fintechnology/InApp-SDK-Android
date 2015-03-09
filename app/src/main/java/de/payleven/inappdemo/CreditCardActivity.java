package de.payleven.inappdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.payleven.inappsdk.CreditCardPaymentInstrument;
import de.payleven.inappsdk.errors.CallbackError;
import de.payleven.inappsdk.errors.ValidationError;
import de.payleven.inappsdk.errors.causes.ErrorCause;
import de.payleven.inappsdk.errors.causes.InvalidCVV;
import de.payleven.inappsdk.errors.causes.InvalidCardHolder;
import de.payleven.inappsdk.errors.causes.InvalidCardNumber;
import de.payleven.inappsdk.errors.causes.InvalidDate;
import de.payleven.inappsdk.errors.causes.InvalidMonth;
import de.payleven.inappsdk.errors.causes.InvalidYear;
import de.payleven.inappsdk.listeners.AddPaymentInstrumentListener;

/**
 * Activity used to add a credit card payment instrument to the logged in user
 */
public class CreditCardActivity extends ToplevelActivity {

    private EditText cardNumberEditText;
    private EditText cardHolderEditText;
    private EditText expirationMonthEditText;
    private EditText expirationYearEditText;
    private EditText cvvEditText;
    private TextView errorTextView;
    private EditText useCaseSelectionView;

    private PaylevenWrapper paylevenWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        paylevenWrapper = PaylevenWrapper.getInstance();
        init();
    }

    private void init() {
        cardNumberEditText = (EditText) findViewById(R.id.card_number_edittext);
        cardHolderEditText = (EditText) findViewById(R.id.card_holder_edittext);
        expirationMonthEditText = (EditText) findViewById(R.id.expiration_month_edittext);
        expirationYearEditText = (EditText) findViewById(R.id.expiration_year_edittext);
        cvvEditText = (EditText) findViewById(R.id.cvv_edittext);
        errorTextView = (TextView) findViewById(R.id.error_textview);
        useCaseSelectionView = (EditText) findViewById(R.id.use_case_view);

        setFocusListeners();

        Button addCreditCardPIButton = (Button) findViewById(R.id.add_cc_button);
        addCreditCardPIButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPaymentInstrument();
                    }
                });
    }

    private void setFocusListeners() {
        cardNumberEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String cardNumber = cardNumberEditText.getText().toString();

                            if (!CreditCardPaymentInstrument.validatePan(cardNumber).isValid()) {
                                ErrorCause errorCause = CreditCardPaymentInstrument.validatePan(
                                        cardNumber).getErrorCause();
                                cardNumberEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        cardHolderEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String cardHolder = cardHolderEditText.getText().toString();

                            if (!CreditCardPaymentInstrument.validateCardHolder(cardHolder)
                                    .isValid()) {
                                ErrorCause errorCause = CreditCardPaymentInstrument
                                        .validateCardHolder(cardHolder).getErrorCause();
                                cardHolderEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        expirationMonthEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {

                            // if the edit text doesn't contain a number, just consider this as 
                            // null value
                            Integer expirationMonth = null;
                            try {
                                expirationMonth = Integer
                                        .parseInt(expirationMonthEditText.getText().toString());
                            } catch (NumberFormatException e) {
                            }
                            if (!CreditCardPaymentInstrument.validateExpiryMonth(expirationMonth)
                                    .isValid()) {
                                ErrorCause errorCause = CreditCardPaymentInstrument
                                        .validateExpiryMonth(expirationMonth).getErrorCause();
                                expirationMonthEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        expirationYearEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {

                            // if the edit text doesn't contain a number, just consider this as 
                            // null value
                            Integer expirationYear = null;
                            try {
                                expirationYear = Integer
                                        .parseInt(expirationYearEditText.getText().toString());
                            } catch (NumberFormatException e) {

                            }

                            if (!CreditCardPaymentInstrument.validateExpiryYear(expirationYear)
                                    .isValid()) {
                                ErrorCause errorCause = CreditCardPaymentInstrument
                                        .validateExpiryYear(expirationYear).getErrorCause();
                                expirationYearEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });

        cvvEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            final String cvv = cvvEditText.getText().toString();
                            if (!CreditCardPaymentInstrument.validateCVV(cvv).isValid()) {
                                ErrorCause errorCause = CreditCardPaymentInstrument.validateCVV(
                                        cvv).getErrorCause();
                                cvvEditText.setError(errorCause.getErrorMessage());
                            }
                        }
                    }
                });
    }

    @Nullable
    private CreditCardPaymentInstrument getPaymentInstrument() {
        final String cardNumber = cardNumberEditText.getText().toString();
        final String cardHolder = cardHolderEditText.getText().toString();

        Integer expirationMonth = null;

        try {
            expirationMonth = Integer.parseInt(
                    expirationMonthEditText.getText().toString());
        } catch (NumberFormatException e) {

        }
        Integer expirationYear = null;
        try {
            expirationYear = Integer.parseInt(
                    expirationYearEditText.getText().toString());
        } catch (NumberFormatException e) {

        }


        final String cvv = cvvEditText.getText().toString();

        return new CreditCardPaymentInstrument(
                cardNumber, cardHolder, expirationMonth, expirationYear, cvv);
    }

    /**
     * Request the {@link de.payleven.inappdemo.PaylevenWrapper} to add a payment instrument.
     * Displays a progress dialog while the action is being done.
     * Handles success and failure cases.
     */
    private void addPaymentInstrument() {
        showProgressDialog();

        final CreditCardPaymentInstrument paymentInstrument = getPaymentInstrument();
        final String useCase = useCaseSelectionView.getText().toString();


        paylevenWrapper.addPaymentInstrument(
                paymentInstrument, useCase, new AddPaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentAddedSuccessfully(String userToken) {
                        dismissProgressDialog();
                        paylevenWrapper.saveUserToken(userToken);
                        Toast.makeText(
                                CreditCardActivity.this, R.string.cc_added,
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
            if (errorCause instanceof InvalidCardNumber) {
                cardNumberEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidCardHolder) {
                cardHolderEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidDate) {
                expirationMonthEditText.setError(errorMessage);
                expirationYearEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidYear) {
                expirationYearEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidMonth) {
                expirationMonthEditText.setError(errorMessage);
            } else if (errorCause instanceof InvalidCVV) {
                cvvEditText.setError(errorMessage);
            }
        }
    }

}
