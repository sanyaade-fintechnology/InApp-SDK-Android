package de.payleven.inappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

import de.payleven.inappsdk.PaylevenFactory;
import de.payleven.inappsdk.PaylevenInAppClient;

/**
 * This activity simulates a login activity in an usual integrator app.
 * When the user presses "Login", the registration with the
 * {@link de.payleven.inappsdk.PaylevenInAppClient} is triggered.
 */
public class LoginActivity extends ToplevelActivity {

    private static final String REGEX_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$";

    private EditText emailEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String error = emailValidation(getEmail());
                        if(error != null){
                            emailEditText.setError(error);
                        }else {
                            registerWithPaylevenClient();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.EXTRA_EMAIL, getEmail());
                            startActivity(intent);
                        }
                    }
                });

    }

    /**
     * Register with {@link de.payleven.inappsdk.PaylevenInAppClient} and initialize the
     * {@link de.payleven.inappdemo.PaylevenWrapper}
     */
    private void registerWithPaylevenClient() {
        PaylevenInAppClient paylevenInAppClient = PaylevenFactory.registerWithAPIKey(
                this, PaylevenWrapper.API_KEY);
        PaylevenWrapper.getInstance().initPaylevenWrapper(
                LoginActivity.this, paylevenInAppClient);
    }

    private String getEmail() {
        return emailEditText.getText().toString();
    }

    private String emailValidation(String email) {
        if (null == email || email.isEmpty()) {
            return getString(R.string.missing_email);
        }
        final Pattern pattern = Pattern.compile(REGEX_PATTERN, Pattern.CASE_INSENSITIVE);

        if (!pattern.matcher(email).matches()) {
            return getString(R.string.invalid_email);
        }
        return null;

    }

}
