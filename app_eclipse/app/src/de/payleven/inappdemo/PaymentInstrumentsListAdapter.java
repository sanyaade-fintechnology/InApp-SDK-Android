package de.payleven.inappdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.payleven.inappsdk.CreditCardPaymentInstrument;
import de.payleven.inappsdk.PaymentInstrument;
import de.payleven.inappsdk.PaymentInstrumentAction;

/**
 * Adapter for the list of payment instruments
 */
public class PaymentInstrumentsListAdapter extends ArrayAdapter<PaymentInstrument> {

    public interface EditPaymentInstrumentButtonListener {
        void editPaymentInstrument(final PaymentInstrument paymentInstrument,
                                   final PaymentInstrumentAction action,
                                   final String cvv);
    }

    public interface RemovePaymentInstrumentButtonListener {
        void removePaymentInstrument(final PaymentInstrument paymentInstrument);
    }

    public interface SwitchPaymentInstrumentPositionsListener {
        void switchPositions(final int to, final int from);
    }

    private LayoutInflater inflater;
    private Context context;

    private EditPaymentInstrumentButtonListener editPaymentInstrumentListener;
    private RemovePaymentInstrumentButtonListener removePaymentInstrumentButtonListener;
    private SwitchPaymentInstrumentPositionsListener positionListener;

    private List<PaymentInstrument> objects;

    public PaymentInstrumentsListAdapter(
            Context context,
            List<PaymentInstrument> objects,
            EditPaymentInstrumentButtonListener editPaymentInstrumentListener,
            RemovePaymentInstrumentButtonListener removePaymentInstrumentButtonListener,
            SwitchPaymentInstrumentPositionsListener positionListener) {

        super(context, R.layout.payment_instrument_item, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.editPaymentInstrumentListener = editPaymentInstrumentListener;
        this.removePaymentInstrumentButtonListener = removePaymentInstrumentButtonListener;
        this.positionListener = positionListener;
        this.objects = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.payment_instrument_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final PaymentInstrument paymentInstrument = getItem(position);

        setupPaymentInstrumentView(holder.pan, holder.expiryDate, paymentInstrument);

        int textColor = android.R.color.black;
        boolean validateEnabled = false;
        if (PaymentInstrument.Status.INVALID == paymentInstrument.getStatus()) {
            textColor = android.R.color.darker_gray;
            validateEnabled = true;
        }

        holder.cvv.setEnabled(validateEnabled);
        holder.validatePaymentInstrument.setEnabled(validateEnabled);

        holder.expiryDate.setTextColor(context.getResources().getColor(textColor));
        holder.pan.setTextColor(context.getResources().getColor(textColor));

        int blockedVisibility = paymentInstrument.isBlocked() ? View.VISIBLE : View.GONE;
        holder.blocked.setVisibility(blockedVisibility);

        int disableText = R.string.pi_disable;
        if (PaymentInstrument.Status.DISABLED == paymentInstrument.getStatus()) {
            disableText = R.string.pi_enable;
        }
        holder.disablePaymentInstrument.setText(disableText);

        holder.disablePaymentInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentInstrumentAction action;
                if (PaymentInstrument.Status.DISABLED == paymentInstrument.getStatus()) {
                    action = PaymentInstrumentAction.ENABLE;
                } else {
                    action = PaymentInstrumentAction.DISABLE;
                }
                editPaymentInstrumentListener.editPaymentInstrument(paymentInstrument,
                        action, null);
            }
        });

        holder.removePaymentInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePaymentInstrumentButtonListener.removePaymentInstrument(paymentInstrument);
            }
        });

        holder.validatePaymentInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cvv = holder.cvv.getText().toString();
                editPaymentInstrumentListener.editPaymentInstrument(paymentInstrument,
                        PaymentInstrumentAction.VALIDATE, cvv);
            }
        });
        int moveUpVisiblity = position == 0 ? View.INVISIBLE : View.VISIBLE;
        holder.moveUp.setVisibility(moveUpVisiblity);

        int moveDownVisibility = position == getCount() - 1 ? View.INVISIBLE : View.VISIBLE;
        holder.moveDown.setVisibility(moveDownVisibility);

        holder.moveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionListener.switchPositions(position - 1, position);
            }
        });

        holder.moveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionListener.switchPositions(position, position + 1);


            }
        });
        return convertView;
    }

    /**
     * Construct a description for a payment instrument based on the type of the payment instrument
     *
     * @param paymentInstrument
     * @return
     */
    private void setupPaymentInstrumentView(
            TextView pan,
            TextView expiryDate,
            final PaymentInstrument paymentInstrument) {
        switch (paymentInstrument.getPaymentInstrumentType()) {
            case CC:
                final CreditCardPaymentInstrument creditCardPaymentInstrument =
                        (CreditCardPaymentInstrument) paymentInstrument;
                pan.setText(creditCardPaymentInstrument.getPanMasked());
                final String date = creditCardPaymentInstrument.getExpiryMonth()
                        + "/" + creditCardPaymentInstrument.getExpiryYear();
                expiryDate.setText(date);
        }
    }


    private static class ViewHolder {
        TextView pan;
        TextView expiryDate;
        TextView blocked;
        Button disablePaymentInstrument;
        Button removePaymentInstrument;
        ImageView moveUp;
        ImageView moveDown;
        EditText cvv;
        Button validatePaymentInstrument;

        private ViewHolder(View itemView) {
            expiryDate = (TextView) itemView.findViewById(
                    R.id.expiry_date);
            pan = (TextView) itemView.findViewById(R.id.pan);
            blocked = (TextView) itemView.findViewById(R.id.blocked);
            disablePaymentInstrument = (Button) itemView.findViewById(
                    R.id.disable_payment_instrument);
            removePaymentInstrument = (Button) itemView.findViewById(
                    R.id.remove_payment_instrument);
            cvv = (EditText) itemView.findViewById(R.id.cvv);
            validatePaymentInstrument = (Button) itemView.findViewById(R.id.validate);

            moveUp = (ImageView) itemView.findViewById(R.id.move_up);
            moveDown = (ImageView) itemView.findViewById(R.id.move_down);
        }
    }

    public List<PaymentInstrument> getObjects() {
        return objects;
    }


}
