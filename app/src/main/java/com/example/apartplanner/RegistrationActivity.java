package com.example.apartplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    Button registrBtn;
    EditText edMail,edPassword;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        registrBtn = findViewById(R.id.btnRegistr);
        edMail = findViewById(R.id.registrMail);
        edPassword = findViewById(R.id.passwordRegistr);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        registrBtn.setOnClickListener(view -> {
            if (!edMail.getText().toString().equals("") && !edPassword.getText().toString().equals("")) {
                firebaseAuth.createUserWithEmailAndPassword(edMail.getText().toString(), edPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            DocumentReference df = firestore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("UserEmail", edMail.getText().toString());
                            userInfo.put("Password", edPassword.getText().toString());
                            userInfo.put("isAdmin", false);
                            df.set(userInfo);
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            Toast.makeText(getApplicationContext(), "Вы зашли, как " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show());
            }else{
                Toast.makeText(RegistrationActivity.this, "Заполните пустые поля", Toast.LENGTH_SHORT).show();
            }
        });
    }
}