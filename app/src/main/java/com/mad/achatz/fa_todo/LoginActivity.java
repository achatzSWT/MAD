package com.mad.achatz.fa_todo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final int RESULT_NO_CONNECTION = 123;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private ProgressDialog progressDialog;
    private Button loginButton;

    private boolean authentificationFail = false;

    /**
     * Dieser Textwatcher wird genutzt um bei jeder Eingabe zu überprüfen, ob eines der Textfelder leer ist und gegebenenfalls
     * den Login Button zu deaktivieren.
     */
    private TextWatcher emptyTextViewWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (authentificationFail) {
                emailView.setError(null);
                passwordView.setError(null);
                authentificationFail = false;
            }
            if (emailView.getText().toString().isEmpty() || passwordView.getText().toString().isEmpty()) {
                loginButton.setEnabled(false);
            } else {
                loginButton.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Set up the login form.
        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        emailView.addTextChangedListener(emptyTextViewWatcher);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.addTextChangedListener(emptyTextViewWatcher);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.attempt_login));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                authTask.cancel(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // nichts machen, wenn der Zurück Button gedrückt wird.
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (authTask != null) {
            return;
        }

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (!TextUtils.isEmpty(email) && !isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            authTask = new UserLoginTask(email, password);
            authTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        // ^ ... Stringanfang
        // [^@] ... Alle Zeichen außer "@"
        // + ... 1 oder mehrere
        // @ ... @
        // \w+ ... 1 oder mehrere Wort Zeichen [A-Za-z0-9_]
        // \. ... "."
        // [A-Za-z]+ .. 1 oder mehrere Buchstaben
        // $ ... Stringende
        return email.matches("^[^@]+@\\w+\\.[A-Za-z]+$");
    }

    private boolean isPasswordValid(String password) {
        // Genau 6 Zahlen
        return password.matches("^\\d{6}$");
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;

        private final static String URL = "http://10.0.2.2:8080/api/users/auth/";

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);

                // Json String, der zum Server gesendet wird, bereitstellen
                OutputStream outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);
                writer.write(buildJson());
                writer.close();

                // Request an Server senden
                InputStream inputStream = connection.getInputStream();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Ergebnis vom Server auslesen
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String resultString = reader.readLine();

                    result = (resultString != null && resultString.equals("true"));
                }

                connection.disconnect();

            } catch (IOException e) {
                result = null;
            }

            return result;
        }

        private String buildJson() {
            return String.format("{\"email\":\"%s\",\"pwd\":\"%s\"}", email, password);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;
            progressDialog.dismiss();

            if (success == null) {
                // Keine Verbindung zum Server möglich
                setResult(RESULT_NO_CONNECTION);
                finish();
            } else if (success) {
                setResult(RESULT_OK);
                finish();
            } else {
                authentificationFail = true;
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;
            progressDialog.dismiss();
        }
    }
}