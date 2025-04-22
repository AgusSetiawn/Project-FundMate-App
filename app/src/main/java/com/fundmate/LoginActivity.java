package com.fundmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.content.SharedPreferences;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;
    ImageView passwordEye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        passwordEye = findViewById(R.id.password_eye);

        // Set the password field transformation method to hide text by default
        loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Toggle password visibility on icon click
        passwordEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    // Change to plain text
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEye.setImageResource(R.drawable.eye); // Open eye icon
                } else {
                    // Change to password text
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEye.setImageResource(R.drawable.eye_crossed); // Closed eye icon
                }
            }
        });

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() || !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        // Redirect to signup activity
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Validate username field
    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username Tidak Boleh Kosong");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    // Validate password field
    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password Tidak Boleh Kosong");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null); // Clear the error

                    // Get the password from DB
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    // Verify the password using BCrypt
                    if (BCrypt.checkpw(userPassword, passwordFromDB)) {
                        loginUsername.setError(null);

                        // Get other user data from the database
                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);

                        // ✅ Simpan username ke SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", usernameFromDB);
                        editor.apply();

                        // Start MainActivity and pass user data
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                        finish(); // Optional: supaya user tidak bisa balik ke login
                    } else {
                        loginPassword.setError("Password salah");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User tidak tersedia");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Terjadi kesalahan. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}