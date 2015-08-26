package de.payleven.inappdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.payleven.inappsdk.CreditCardPaymentInstrument;
import de.payleven.inappsdk.PaymentInstrument;

/**
 * Adapter for the list of payment instruments
 */
public class PaymentInstrumentsListAdapter extends ArrayAdapter<PaymentInstrument> {

    public interface DisablePaymentInstrumentButtonListener {
        void disablePaymentInstrument(final PaymentInstrument paymentInstrument);
    }

    public interface SwitchPaymentInstrumentPositionsListener {
        void switchPositions(final int to, final int from);
    }

    private LayoutInflater inflater;
    private Context context;

    private DisablePaymentInstrumentButtonListener disablePaymentInstrumentListener;
    private SwitchPaymentInstrumentPositionsListener positionListener;

    private List<PaymentInstrument> objects;

    public PaymentInstrumentsListAdapter(
            Context context,
            List<PaymentInstrument> objects,
            DisablePaymentInstrumentButtonListener disablePaymentInstrumentListener,
            SwitchPaymentInstrumentPositionsListener positionListener) {

        super(context, R.layout.payment_instrument_item, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.disablePaymentInstrumentListener = disablePaymentInstrumentListener;
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

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final PaymentInstrument paymentInstrument = getItem(position);
        holder.paymentInstrumentType.setText(paymentInstrument.getPaymentInstrumentType().name());

        final String description = getDescriptionFromPaymentInstrument(paymentInstrument);
        holder.paymentInstrumentDetails.setText(description);

        holder.disablePaymentInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disablePaymentInstrumentListener.disablePaymentInstrument(paymentInstrument);
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
    private String getDescriptionFromPaymentInstrument(final PaymentInstrument paymentInstrument) {
        switch (paymentInstrument.getPaymentInstrumentType()) {
            case CC:
                final CreditCardPaymentInstrument creditCardPaymentInstrument =
                        (CreditCardPaymentInstrument) paymentInstrument;
                final String descriptionCC = creditCardPaymentInstrument.getPanMasked() + " "
                        + creditCardPaymentInstrument.getExpiryMonth()
                        + "/" + creditCardPaymentInstrument.getExpiryYear();
                return descriptionCC;
            default:
                return null;
        }
    }

    private static class ViewHolder {
        TextView paymentInstrumentType;
        TextView paymentInstrumentDetails;
        Button disablePaymentInstrument;
        ImageView moveUp;
        ImageView moveDown;

        private ViewHolder(View itemView) {
            paymentInstrumentDetails = (TextView) itemView.findViewById(
                    R.id.payment_instrument_details);
            paymentInstrumentType = (TextView) itemView.findViewById(R.id.payment_instrument_type);
            disablePaymentInstrument = (Button) itemView.findViewById(
                    R.id.disable_payment_instrument);
            moveUp = (ImageView) itemView.findViewById(R.id.move_up);
            moveDown = (ImageView) itemView.findViewById(R.id.move_down);
        }
    }

    public List<PaymentInstrument> getObjects() {
        return objects;
    }


}
