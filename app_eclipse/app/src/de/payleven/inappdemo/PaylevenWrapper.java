package de.payleven.inappdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.List;

import de.payleven.inappsdk.PaylevenApi;
import de.payleven.inappsdk.PaymentInstrument;
import de.payleven.inappsdk.listeners.AddPaymentInstrumentListener;
import de.payleven.inappsdk.listeners.DisablePaymentInstrumentListener;
import de.payleven.inappsdk.listeners.GetPaymentInstrumentsListener;
import de.payleven.inappsdk.listeners.RemovePaymentInstrumentFromUseCaseListener;
import de.payleven.inappsdk.listeners.SetPaymentInstrumentsOrderListener;

/**
 * Wrapper for the payleven API
 */
public class PaylevenWrapper {

    public static final String API_KEY = BuildConfig.PAYLEVEN_API_KEY;

    private static final String SHARED_PREFERENCES_FILE =
            "de.payleven.inappdemo.SHARED_PREFERENCES_FILE";

    private static PaylevenWrapper instance;

    private PaylevenApi paylevenApi;
    private String email;

    private Context applicationContext;

    public static PaylevenWrapper getInstance() {
        if (null == instance) {
            instance = new PaylevenWrapper();
        }
        return instance;
    }

    public void initPaylevenWrapper(Context context, PaylevenApi paylevenApi) {
        this.applicationContext = context.getApplicationContext();
        this.paylevenApi = paylevenApi;
    }

    public void saveUserToken(final String userToken) {
        saveInSharedPreferences(email, userToken);
    }

    private void saveInSharedPreferences(final String email, final String userToken) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(email, userToken);
        editor.commit();
    }

    private String getUserToken() {
        if (null != email) {
            return getUserTokenFromSharedPreferences(email);
        }
        return null;
    }

    private String getUserTokenFromSharedPreferences(final String email) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(email, null);
    }

    public void addPaymentInstrument(final PaymentInstrument paymentInstrument,
                                     @Nullable final String useCase,
                                     AddPaymentInstrumentListener listener) {

        String userToken = getUserToken();
        if (null == userToken) {
            paylevenApi.createUserTokenWithPaymentInstrument(
                    email,
                    paymentInstrument,
                    useCase,
                    listener);
        } else {
            paylevenApi.addPaymentInstrument(userToken, paymentInstrument, useCase, listener);
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void getPaymentInstruments(final String useCase,
                                      GetPaymentInstrumentsListener listener) {
        paylevenApi.getPaymentInstrumentsList(getUserToken(), useCase, listener);
    }

    public void setPaymentInstrumentsOrder(final String useCase,
                                           final List<PaymentInstrument> paymentInstruments,
                                           SetPaymentInstrumentsOrderListener listener) {
        paylevenApi.setPaymentInstrumentsOrder(
                getUserToken(),
                useCase,
                paymentInstruments,
                listener);
    }

    public void disablePaymentInstrument(final PaymentInstrument paymentInstrument,
                                         DisablePaymentInstrumentListener listener) {
        paylevenApi.disablePaymentInstrument(getUserToken(), paymentInstrument, listener);
    }

    public void removePaymentInstrumentFromUseCase(
            final PaymentInstrument paymentInstrument,
            final String useCase,
            final RemovePaymentInstrumentFromUseCaseListener listener) {

        paylevenApi.removePaymentInstrumentFromUseCase(
                getUserToken(),
                paymentInstrument,
                useCase,
                listener);
    }
}
