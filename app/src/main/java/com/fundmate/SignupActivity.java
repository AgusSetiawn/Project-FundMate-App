package com.fundmate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    ImageView passwordEye;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        passwordEye = findViewById(R.id.password_eye);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        // Menangani klik untuk toggle password visibility
        passwordEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signupPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    // Ubah menjadi teks biasa
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEye.setImageResource(R.drawable.eye);  // Ganti ke ikon mata terbuka
                } else {
                    // Ubah menjadi teks tersembunyi
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEye.setImageResource(R.drawable.eye_crossed);  // Ganti ke ikon mata tertutup
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                // Validasi apakah semua field sudah diisi
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Harap isi semua Data!", Toast.LENGTH_SHORT).show();
                    return; // Membatalkan proses jika ada field yang kosong
                }

                if (!isValidEmail(email)) {
                    // Membuat Toast jika email tidak valid
                    Toast.makeText(SignupActivity.this, "Email tidak valid! Pastikan email mengandung domain seperti @gmail.com atau @yahoo.com", Toast.LENGTH_SHORT).show();
                    return; // Membatalkan proses jika email tidak valid
                }

                // Cek jika email atau username sudah terdaftar
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                // Mengecek apakah username sudah terdaftar
                reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Jika username sudah terdaftar, tampilkan pesan kesalahan
                            Toast.makeText(SignupActivity.this, "Akun sudah terdaftar dengan username ini!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Jika username belum terdaftar, cek email
                            reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Jika email sudah terdaftar, tampilkan pesan kesalahan
                                        Toast.makeText(SignupActivity.this, "Akun sudah terdaftar dengan email ini!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Jika email dan username belum terdaftar, simpan data baru
                                        HelperClass helperClass = new HelperClass(name, email, username, password);
                                        reference.child(username).setValue(helperClass);

                                        Toast.makeText(SignupActivity.this, "Sign Up berhasil!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error
                                    Toast.makeText(SignupActivity.this, "Terjadi kesalahan. Coba lagi.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                        Toast.makeText(SignupActivity.this, "Terjadi kesalahan. Coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fungsi untuk memvalidasi format email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    // Fungsi untuk melakukan hashing terhadap password menggunakan BCrypt
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
