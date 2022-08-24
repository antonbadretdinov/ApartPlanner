package com.example.apartplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AdminActivity extends AppCompatActivity implements AdminAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    AdminAdapter adminAdapter;
    ProgressBar progressCircle;

    FirebaseStorage storage;
    DatabaseReference databaseReference;
    ValueEventListener dBListener;

    List<UploadImageActivity> uploadList;

    RecyclerView recyclerViewStudio;
    StudioAdminAdapter studioAdminAdapter;
    ArrayList<Studio> studioList;

    Toolbar toolbar;
    private Object AdminAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Режим администратора");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        progressCircle = findViewById(R.id.progressCircleAdmin);

        recyclerView = findViewById(R.id.adressPageRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));


        studioList = new ArrayList<>();



        uploadList = new ArrayList<>();
        adminAdapter = new AdminAdapter(AdminActivity.this,uploadList);
        recyclerView.setAdapter(adminAdapter);
        adminAdapter.setOnItemClickListener(AdminActivity.this);

        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        dBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    UploadImageActivity upload = postSnapshot.getValue(UploadImageActivity.class);
                    assert upload != null;
                    studioList = upload.getStudioList();
                    upload.setKey(postSnapshot.getKey());
                    uploadList.add(upload);
                }
                adminAdapter.setKey(snapshot.getKey());
                adminAdapter.notifyDataSetChanged();
                adminAdapter.setStudios(studioList);
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
        getMenuInflater().inflate(R.menu.menu_admin,menu);
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
        UploadImageActivity selectedItem = uploadList.get(position);
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

