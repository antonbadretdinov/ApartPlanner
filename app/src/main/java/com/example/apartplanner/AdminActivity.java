package com.example.apartplanner;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.apartplanner.adapter.AdminAdapter;
import com.example.apartplanner.adapter.StudioAdminAdapter;
import com.example.apartplanner.model.Studio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity implements AdminAdapter.OnItemClickListener {

    ViewPager2 viewPager;
    AdminAdapter adminAdapter;
    ProgressBar progressCircle;

    FirebaseStorage storage;
    DatabaseReference databaseReference;
    ValueEventListener dBListener;

    List<Address> uploadList;

    Toolbar toolbar;
    private Object AdminAdapter;

    @SuppressLint("NotifyDataSetChanged")
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

        uploadList = new ArrayList<>();
        adminAdapter = new AdminAdapter(uploadList);
        viewPager.setAdapter(adminAdapter);
        adminAdapter.setOnItemClickListener(AdminActivity.this);

        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        dBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Address address = postSnapshot.getValue(Address.class);
                    if (address == null) continue;
//                    studioList = upload.getStudioList();
                    address.setKey(postSnapshot.getKey());
                    uploadList.add(address);
                }
                adminAdapter.setKey(snapshot.getKey());
                adminAdapter.notifyDataSetChanged();
                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.makeNew) {
            Intent intent = new Intent(this, AdressActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteClick(int position) {
        Address selectedItem = uploadList.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference storageRef = storage.getReferenceFromUrl(selectedItem.getImageUrl());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(AdminActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dBListener);
    }

}

