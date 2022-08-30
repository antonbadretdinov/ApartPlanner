package com.example.apartplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.apartplanner.adapter.AddressAdapter;
import com.example.apartplanner.model.Address;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    AddressAdapter addressAdapter;
    ProgressBar progressCircle;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    DatabaseReference databaseReference;

    Toolbar toolbar;

    private final AddressAdapter.AddressAdapterEventListener adapterEventListener = new AddressAdapter.AddressAdapterEventListener() {
        @Override
        public void onDataChanged() {
            progressCircle.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError(DatabaseError e) {
            progressCircle.setVisibility(View.INVISIBLE);
            Toast.makeText(UserActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Все планировки");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        progressCircle = findViewById(R.id.progressCircle);

        viewPager = findViewById(R.id.viewPager);

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        FirebaseRecyclerOptions<Address> options = new FirebaseRecyclerOptions.Builder<Address>()
                .setQuery(databaseReference, Address.class)
                .build();

        addressAdapter = new AddressAdapter(options, adapterEventListener);
        viewPager.setAdapter(addressAdapter);
        addressAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logIn) {

            DocumentReference df = fStore.collection("Users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
            df.get().addOnSuccessListener(documentSnapshot -> {
                if(Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"))){
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(UserActivity.this, "Ошибка доступа", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(UserActivity.this, "Ошибка доступа", Toast.LENGTH_SHORT).show());

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addressAdapter.stopListening();
    }
}