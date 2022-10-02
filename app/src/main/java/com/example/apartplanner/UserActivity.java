package com.example.apartplanner;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.io.FileNotFoundException;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {


    private static final int STORAGE_PERMISSION_CODE = 100;

    ViewPager2 viewPager;
    AddressAdapter addressAdapter;
    ProgressBar progressCircle;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    DatabaseReference databaseReference;

    Toolbar toolbar;

    Address addressItem;
    DatabaseReference dbRef;

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

        @Override
        public void onGeneratePdf(Address item, DatabaseReference ref) {
            addressItem = item;
            dbRef = ref;
                DocumentReference df = fStore.collection("Users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                df.get().addOnSuccessListener(documentSnapshot -> {
                    if(Boolean.TRUE.equals(documentSnapshot.getBoolean("isPdfAccess"))){
                        if(checkPermission()){
                            try {
                                generatePdf(item,ref);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }else{
                            requestPermission();
                        }
                    }else{
                        Toast.makeText(UserActivity.this, "У вас нет доступа к этой функции", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(UserActivity.this, "Ошибка доступа", Toast.LENGTH_SHORT).show());
        }
    };

    private void generatePdf(Address item, DatabaseReference ref) throws FileNotFoundException {
        PdfActivity pdfActivity = new PdfActivity(this,ref,item);
        pdfActivity.generatePDF();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        fAuth = FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Все планировки");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

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
                    Toast.makeText(UserActivity.this, "У вас нет доступа к этой функции", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(UserActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show());

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addressAdapter.stopListening();
    }

    private void requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);

            }catch (Exception e){
                Toast.makeText(this, "Ошибка"+e, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE
            },STORAGE_PERMISSION_CODE);
        }
    }

    public ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this, "Доступ предоставлен", Toast.LENGTH_SHORT).show();
                        try {
                            generatePdf(addressItem,dbRef);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(this, "Доступ не предоставлен", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    public boolean checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }else{
            int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length>0){
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if(write&&read){
                    try {
                        generatePdf(addressItem,dbRef);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(this, "Доступ не предоставлен", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Доступ не предоставлен", Toast.LENGTH_SHORT).show();
        }
    }

}