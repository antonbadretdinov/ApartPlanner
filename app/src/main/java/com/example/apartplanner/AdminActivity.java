package com.example.apartplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.apartplanner.adapter.AdminAdapter;
import com.example.apartplanner.model.Address;
import com.example.apartplanner.model.Studio;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    AdminAdapter adminAdapter;
    ProgressBar progressCircle;

    FirebaseStorage storage;
    DatabaseReference databaseReference;

    Toolbar toolbar;

    AdminAdapter.AdminAdapterEventListener adapterEventListener = new AdminAdapter.AdminAdapterEventListener() {
        @Override
        public void onDataChanged() {
            progressCircle.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError(DatabaseError e) {
            progressCircle.setVisibility(View.INVISIBLE);
            Toast.makeText(AdminActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDeleteClick(Address address, DatabaseReference ref) {
            StorageReference storageRef = storage.getReferenceFromUrl(address.getImageUrl());
            storageRef.delete().addOnSuccessListener(unused -> {
                ref.removeValue();
                Toast.makeText(AdminActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onStudioUpdate(DatabaseReference addressRef, Studio studio) {
            addressRef.child("studioList").child(String.valueOf(studio.getId())).setValue(studio);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Режим администратора");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        progressCircle = findViewById(R.id.progressCircleAdmin);

        viewPager = findViewById(R.id.viewPager);

        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        FirebaseRecyclerOptions<Address> options = new FirebaseRecyclerOptions.Builder<Address>()
                .setQuery(databaseReference, snapshot -> {
                    Address address = snapshot.getValue(Address.class);
                    if (address == null)
                        throw new NullPointerException(String.format("%s value is null", snapshot.getKey()));
                    for (int i = 0; i < address.getStudioList().size(); i++) {
                        address.getStudioList().get(i).setId(i);
                    }
                    return address;
                })
                .build();


        adminAdapter = new AdminAdapter(options, adapterEventListener);
        viewPager.setAdapter(adminAdapter);
        adminAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
//            Intent intent = new Intent(this, UserActivity.class);
//            startActivity(intent);
            finish();
        }
        if (item.getItemId() == R.id.makeNew) {
            Intent intent = new Intent(this, AdressActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adminAdapter.stopListening();
    }
}

