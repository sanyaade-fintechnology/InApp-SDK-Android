package de.payleven.inappdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.payleven.inappsdk.PaymentInstrument;
import de.payleven.inappsdk.errors.CallbackError;
import de.payleven.inappsdk.listeners.DisablePaymentInstrumentListener;
import de.payleven.inappsdk.listeners.GetPaymentInstrumentsListener;
import de.payleven.inappsdk.listeners.RemovePaymentInstrumentFromUseCaseListener;
import de.payleven.inappsdk.listeners.SetPaymentInstrumentsOrderListener;

/**
 * Activity used to request and display the list of payment instruments for the active user
 * depending on the use case
 */
public class PaymentInstrumentsActivity extends FragmentActivity implements
        PaymentInstrumentsListAdapter.DisablePaymentInstrumentButtonListener,
        PaymentInstrumentsListAdapter.RemovePaymentInstrumentButtonListener,
        PaymentInstrumentsListAdapter.SwitchPaymentInstrumentPositionsListener {

    private ListView paymentInstrumentsListView;
    private TextView emptyListTextView;
    private EditText useCaseSelectionView;
    private ProgressDialogFragment progressDialogFragment;

    private PaymentInstrumentsListAdapter listAdapter;

    private PaylevenWrapper paylevenAPI;

    private List<PaymentInstrument> paymentInstruments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_instruments);
        paylevenAPI = PaylevenWrapper.getInstance();
        paymentInstruments = new ArrayList<PaymentInstrument>();
        initUI();
    }

    private void initUI() {
        useCaseSelectionView = (EditText) findViewById(R.id.use_case_filter);
        paymentInstrumentsListView = (ListView) findViewById(R.id.payment_instruments);
        emptyListTextView = (TextView) findViewById(R.id.empty);
        paymentInstrumentsListView.setEmptyView(emptyListTextView);

        final Button getPaymentInstruments = (Button) findViewById(R.id.get_PI_button);
        getPaymentInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentInstruments();
            }
        });
        final Button setPaymentInstruments = (Button) findViewById(R.id.set_PI_button);
        setPaymentInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaymentInstrumentsOrder();
            }
        });
    }

    private void getPaymentInstruments() {
        showProgressDialog();
        final String useCase = useCaseSelectionView.getText().toString();

        paylevenAPI.getPaymentInstruments(useCase, new GetPaymentInstrumentsListener() {
            @Override
            public void onPaymentInstrumentsRetrieved(List<PaymentInstrument> paymentInstruments) {
                dismissProgressDialog();
                PaymentInstrumentsActivity.this.paymentInstruments = paymentInstruments;
                updateListView(useCase);
            }

            @Override
            public void onPaymentInstrumentsRetrieveFailed(Throwable throwable) {
                dismissProgressDialog();
                paymentInstruments.clear();
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                String errorText;
                if(throwable instanceof CallbackError){
                    errorText = ((CallbackError)throwable).getErrorCode() 
                            + " " + throwable.getMessage();
                }else{
                    errorText = throwable.getMessage();
                }
                emptyListTextView.setText(errorText);
            }
        });
    }

    private void updateListView(String useCase) {
        listAdapter = new PaymentInstrumentsListAdapter(
                PaymentInstrumentsActivity.this,
                paymentInstruments,
                useCase,
                PaymentInstrumentsActivity.this,
                PaymentInstrumentsActivity.this,
                PaymentInstrumentsActivity.this);
        paymentInstrumentsListView.setAdapter(listAdapter);
        if (paymentInstruments.size() == 0) {
            emptyListTextView.setText(getString(R.string.no_pi));
        }
    }


    private void showProgressDialog() {
        final FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.addToBackStack(null);

        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(fTransaction, "fragment_progress");
    }

    private void dismissProgressDialog() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
    }

    private void setPaymentInstrumentsOrder() {
        if (listAdapter == null)
            return;

        showProgressDialog();

        paylevenAPI.setPaymentInstrumentsOrder(
                listAdapter.getUseCase(),
                listAdapter.getObjects(),
                new SetPaymentInstrumentsOrderListener() {
                    @Override
                    public void onPaymentInstrumentsSetSuccessfully(
                            int numberOfPaymentInstrumentsReordered) {
                        dismissProgressDialog();
                        Toast.makeText(PaymentInstrumentsActivity.this,
                                R.string.pi_order,
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPaymentInstrumentsSetFailed(Throwable throwable) {
                        dismissProgressDialog();
                        Toast.makeText(PaymentInstrumentsActivity.this,
                                getString(R.string.pi_order_failed, throwable.getMessage()),
                                Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    public void disablePaymentInstrument(final PaymentInstrument paymentInstrument) {
        showProgressDialog();
        paylevenAPI.disablePaymentInstrument(
                paymentInstrument,
                new DisablePaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentDisabledSuccessfully() {
                        dismissProgressDialog();
                        // request again the list of payment instruments to make sure that the
                        // payment instrument was disabled
                        getPaymentInstruments();
                    }

                    @Override
                    public void onPaymentInstrumentDisableFailed(Throwable throwable) {
                        dismissProgressDialog();
                        Toast.makeText(PaymentInstrumentsActivity.this,
                                getString(R.string.pi_disabling_failed,
                                        throwable.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void removePaymentInstrument(final PaymentInstrument paymentInstrument,
                                        final String useCase) {
        showProgressDialog();

        paylevenAPI.removePaymentInstrumentFromUseCase(
                paymentInstrument,
                useCase,
                new RemovePaymentInstrumentFromUseCaseListener() {
                    @Override
                    public void onPaymentInstrumentRemovedSuccessfully() {
                        dismissProgressDialog();
                        // request again the list of payment instruments to make sure that the
                        // payment instrument was disabled
                        getPaymentInstruments();
                    }

                    @Override
                    public void onPaymentInstrumentRemoveFailed(Throwable throwable) {
                        dismissProgressDialog();
                        Toast.makeText(PaymentInstrumentsActivity.this,
                                getString(R.string.pi_removing_failed,
                                        throwable.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void switchPositions(int to, int from) {
        PaymentInstrument paymentInstrumentTo = paymentInstruments.get(to);
        PaymentInstrument paymentInstrumentFrom = paymentInstruments.get(from);

        paymentInstruments.set(to, paymentInstrumentFrom);
        paymentInstruments.set(from, paymentInstrumentTo);
        listAdapter.notifyDataSetChanged();
    }
}
