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
import de.payleven.inappsdk.DebitCardPaymentInstrument;
import de.payleven.inappsdk.PayPalPaymentInstrument;
import de.payleven.inappsdk.PaymentInstrument;
import de.payleven.inappsdk.SepaPaymentInstrument;

/**
 * Adapter for the list of payment instruments
 */
public class PaymentInstrumentsListAdapter extends ArrayAdapter<PaymentInstrument> {

    public interface DisablePaymentInstrumentButtonListener {
        void disablePaymentInstrument(final PaymentInstrument paymentInstrument);
    }

    public interface RemovePaymentInstrumentButtonListener {
        void removePaymentInstrument(final PaymentInstrument paymentInstrument,
                                     final String useCase);
    }

    public interface SwitchPaymentInstrumentPositionsListener {
        void switchPositions(final int to, final int from);
    }

    private LayoutInflater inflater;
    private Context context;

    private DisablePaymentInstrumentButtonListener disablePaymentInstrumentListener;
    private RemovePaymentInstrumentButtonListener removeListener;
    private SwitchPaymentInstrumentPositionsListener positionListener;

    // the use case of the items
    private final String useCase;
    private List<PaymentInstrument> objects;

    public PaymentInstrumentsListAdapter(
            Context context,
            List<PaymentInstrument> objects,
            final String useCase,
            DisablePaymentInstrumentButtonListener disablePaymentInstrumentListener,
            RemovePaymentInstrumentButtonListener removeListener,
            SwitchPaymentInstrumentPositionsListener positionListener) {

        super(context, R.layout.payment_instrument_item, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.useCase = useCase;
        this.disablePaymentInstrumentListener = disablePaymentInstrumentListener;
        this.removeListener = removeListener;
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


        String removeFromUseCaseText;
        if (useCase != null && !useCase.isEmpty()) {
            removeFromUseCaseText = context.getString(R.string.pi_remove_from, useCase);
        } else {
            removeFromUseCaseText = context.getString(R.string.pi_remove);
        }
        holder.removePaymentInstrument.setText(removeFromUseCaseText);
        holder.removePaymentInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.removePaymentInstrument(paymentInstrument, useCase);
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
                final String descriptionCC = ((CreditCardPaymentInstrument) paymentInstrument)
                        .getPan() + " "
                        + ((CreditCardPaymentInstrument) paymentInstrument).getCardHolder();
                return descriptionCC;
            case DD:
                final String descriptionDD =
                        ((DebitCardPaymentInstrument) paymentInstrument).getAccountNumber() + " "
                                + ((DebitCardPaymentInstrument) paymentInstrument).
                                getRoutingNumber();
                return descriptionDD;
            case SEPA:
                final String descriptionSEPA = ((SepaPaymentInstrument) paymentInstrument).getIban()
                        + " " + ((SepaPaymentInstrument) paymentInstrument).getBic();
                return descriptionSEPA;
            case PAYPAL:
                return ((PayPalPaymentInstrument) paymentInstrument).getAuthToken();
            default:
                return null;
        }
    }

    private static class ViewHolder {
        TextView paymentInstrumentType;
        TextView paymentInstrumentDetails;
        Button disablePaymentInstrument;
        Button removePaymentInstrument;
        ImageView moveUp;
        ImageView moveDown;

        private ViewHolder(View itemView) {
            paymentInstrumentDetails = (TextView) itemView.findViewById(
                    R.id.payment_instrument_details);
            paymentInstrumentType = (TextView) itemView.findViewById(R.id.payment_instrument_type);
            disablePaymentInstrument = (Button) itemView.findViewById(
                    R.id.disable_payment_instrument);
            removePaymentInstrument = (Button) itemView.findViewById(
                    R.id.remove_payment_instrument);
            moveUp = (ImageView) itemView.findViewById(R.id.move_up);
            moveDown = (ImageView) itemView.findViewById(R.id.move_down);
        }
    }

    public String getUseCase() {
        return useCase;
    }

    public List<PaymentInstrument> getObjects() {
        return objects;
    }


}
