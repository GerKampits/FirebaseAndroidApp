package gr.aueb.cf.firebaseproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import gr.aueb.cf.firebaseproject.MainActivity;
import gr.aueb.cf.firebaseproject.R;
import gr.aueb.cf.firebaseproject.helpers.FirebaseReferencesHelper;
import gr.aueb.cf.firebaseproject.helpers.SharedPreferencesHelper;
import gr.aueb.cf.firebaseproject.models.UserModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextInputEditText tvRegister;
    private FirebaseAuth auth;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        //check if user has already logged in
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Email field is empty");
                    etEmail.requestFocus();
                    return;
                }


                if (password.isEmpty()) {
                    etPassword.setError("Password field is empty");
                    etPassword.requestFocus();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                            String userId = auth.getCurrentUser().getUid();
                            String username = sharedPreferencesHelper.getName();

                            FirebaseReferencesHelper.getUserReference(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        } else {
                                            String dateStamp = "";
                                            String imageUrl = "";
                                            UserModel userModel = new UserModel(userId, email, username, dateStamp, imageUrl);

                                            // save user into firebase (realtime database)
                                        FirebaseReferencesHelper.getUserReference(userId).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Please complete verification", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });


        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}