package de.payleven.inappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.payleven.inappsdk.PaylevenApi;
import de.payleven.inappsdk.PaylevenApiFactory;
import de.payleven.inappsdk.listeners.RegistrationListener;

/**
 * Fakes a login activity in an integrator app and registers with payleven
 */
public class LoginActivity extends ToplevelActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        initPaylevenApi();
                    }
                });

    }

    private void initPaylevenApi() {
        PaylevenApiFactory.registerWithAPIKey(
                this, PaylevenWrapper.API_KEY, new RegistrationListener() {
                    @Override
                    public void onRegistrationSuccessful(PaylevenApi paylevenApi) {
                        PaylevenWrapper.getInstance().initPaylevenWrapper(
                                LoginActivity.this, paylevenApi);
                        dismissProgressDialog();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_EMAIL, getEmail());
                        startActivity(intent);
                    }

                    @Override
                    public void onRegistrationFailed(Throwable throwable) {
                        dismissProgressDialog();
                        showErrorAlertDialog(getString(R.string.error), throwable.getMessage());
                    }
                });

    }

    private String getEmail() {
        EditText emailEditText = (EditText) findViewById(R.id.email_edittext);
        return emailEditText.getText().toString();
    }

    /*package*/ void showErrorAlertDialog(final String title, final String message) {
        final SimpleDialogFragment errorFragment = SimpleDialogFragment.newInstance(title, message);
        errorFragment.show(getSupportFragmentManager(), SimpleDialogFragment.TAG);
    }
}
