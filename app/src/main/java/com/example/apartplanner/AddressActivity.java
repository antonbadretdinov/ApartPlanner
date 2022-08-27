package com.example.apartplanner;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.apartplanner.model.Address;
import com.example.apartplanner.model.Studio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class AddressActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imgUri;
    Button chooseImageBtn;
    Button uploadBtn;
    ImageView adressImg;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask uploadTask;

    EditText adressEditText;

    Button logInBtn;
    Toolbar toolbar;

    EditText countOfStudios;

    ArrayList<Studio> studios;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);


        countOfStudios = findViewById(R.id.editCountStudios);

        studios = new ArrayList<>();

        logInBtn = findViewById(R.id.logInButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание нового адреса");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        chooseImageBtn = findViewById(R.id.btnChooseFile);
        uploadBtn = findViewById(R.id.uploadBtn);
        adressImg = findViewById(R.id.imageAdress);
        adressEditText = findViewById(R.id.adressEditText);


        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(AddressActivity.this, "Просходит загрузка", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                    if (!countOfStudios.getText().toString().equals("")) {
                        for (int i = 0; i < Integer.parseInt(countOfStudios.getText().toString()); i++) {
                            studios.add(new Studio(i, "Студия " + (i + 1), "", ""));
                        }
                    }
                    //загрузить студии
                    /*Intent intent = new Intent(AdressActivity.this,AdminActivity.class);
                    intent.putExtra("countOfStudios",countOfStudios.getText().toString());
                    startActivity(intent);*/
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imgUri = data.getData();

            Picasso.with(AddressActivity.this).load(imgUri).into(adressImg);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imgUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddressActivity.this, "Загружено", Toast.LENGTH_SHORT).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Address upload = new Address(adressEditText.getText().toString().trim(), url, studios);
                                    String uploadId = databaseReference.push().getKey();
                                    assert uploadId != null;
                                    databaseReference.child(uploadId).setValue(upload);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddressActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
//            Intent intent = new Intent(this, AdminActivity.class);
//            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
