package com.example.apartplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    Button logInBtn, registrBtn;
    EditText edMail, edPassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInBtn = findViewById(R.id.logInBtn);
        registrBtn = findViewById(R.id.registrBtn);
        edMail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        fAuth = FirebaseAuth.getInstance();

        registrBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(intent);
        });

        logInBtn.setOnClickListener(view -> {
            if (edMail.getText() != null && edPassword.getText() != null) {
                fAuth.signInWithEmailAndPassword(edMail.getText().toString(), edPassword.getText().toString()).addOnSuccessListener(authResult -> {
                    Toast.makeText(getApplicationContext(), "Вы зашли, как " + Objects.requireNonNull(fAuth.getCurrentUser()).getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e ->
                        Toast.makeText(LogInActivity.this, "Ошибка входа, проверьте корректность адреса электронной почты или пароля", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Вы зашли, как " + fAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }
    }
}