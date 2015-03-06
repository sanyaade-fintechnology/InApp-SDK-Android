package de.payleven.inappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main Activity of the sample app.
 */
public class MainActivity extends ActionBarActivity {

    public static final String EXTRA_EMAIL = "email";

    private TextView statusView;
    private String email;

    private PaylevenWrapper paylevenWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        this.email = intent.getStringExtra(EXTRA_EMAIL);

        paylevenWrapper = PaylevenWrapper.getInstance();
        paylevenWrapper.setEmail(email);

        initUiElements();

    }


    private void initUiElements() {
        statusView = (TextView) findViewById(R.id.status_view);

        statusView.setText(getString(R.string.using_email, email));


        Button addCreditCardPIButton = (Button) findViewById(R.id.add_cc_button);
        addCreditCardPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        Button addDebitCardPIButton = (Button) findViewById(R.id.add_debit_button);
        addDebitCardPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DebitCardActivity.class);
                startActivity(intent);
            }
        });

        Button addSepaPIButton = (Button) findViewById(R.id.add_sepa_button);
        addSepaPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SepaActivity.class);
                startActivity(intent);
            }
        });

        Button addPayPalPIButton = (Button) findViewById(R.id.add_paypal_button);
        addPayPalPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PayPalActivity.class);
                startActivity(intent);
            }
        });

        Button getPIListButton = (Button) findViewById(R.id.get_PI_button);
        getPIListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentInstrumentsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
