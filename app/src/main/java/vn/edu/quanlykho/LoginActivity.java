package vn.edu.quanlykho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public final class LoginActivity extends WarehouseActivity {
    private EditText username;
    private EditText password;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        error = findViewById(R.id.login_error);
        findViewById(R.id.sign_in).setOnClickListener(view -> signIn());
        password.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }
            signIn();
            return true;
        });
    }

    private void signIn() {
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString();
        if (enteredUsername.isEmpty() || enteredPassword.trim().isEmpty()) {
            showError(R.string.login_required);
            (enteredUsername.isEmpty() ? username : password).requestFocus();
            return;
        }
        if (!database.checkLogin(enteredUsername, enteredPassword)) {
            showError(R.string.login_invalid);
            password.requestFocus();
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(int message) {
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }
}
