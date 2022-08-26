package com.example.apartplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apartplanner.adapter.AdressAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdressAdapter adressAdapter;
    ProgressBar progressCircle;

    DatabaseReference databaseReference;
    ValueEventListener dBListener;

    List<Address> uploadList;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Все планировки");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        progressCircle = findViewById(R.id.progressCircle);

        recyclerView = findViewById(R.id.userRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        uploadList = new ArrayList<>();

        adressAdapter = new AdressAdapter(UserActivity.this, uploadList);
        recyclerView.setAdapter(adressAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        dBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                uploadList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Address upload = postSnapshot.getValue(Address.class);
                    uploadList.add(upload);
                }

                adressAdapter.notifyDataSetChanged();

                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logIn) {
            Intent intent = new Intent(this, EnterKeyActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}