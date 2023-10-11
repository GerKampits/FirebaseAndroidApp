package gr.aueb.cf.firebaseproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import gr.aueb.cf.firebaseproject.R;
import gr.aueb.cf.firebaseproject.helpers.SharedPreferencesHelper;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private FirebaseAuth auth;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private MaterialTextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
                String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Email field is empty");
                    etEmail.requestFocus();
                    return;
                }

                if (username.isEmpty()) {
                    etUsername.setError("Username field is empty");
                    etUsername.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password field is empty");
                    etPassword.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    etConfirmPassword.setError("Confirm password field is empty");
                    etConfirmPassword.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please enter a valid email");
                    etEmail.requestFocus();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    etConfirmPassword.setError("Passwords do not match");
                    etConfirmPassword.requestFocus();
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            authResult.getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sharedPreferencesHelper.saveData(username, email);
                                    auth.signOut();
                                    Toast.makeText(RegisterActivity.this, "Verification email sent successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Failed to sent verification email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }

            }
        });

    }
}