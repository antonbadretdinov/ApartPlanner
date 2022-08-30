package com.example.apartplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AddressActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imgUri;
    Button chooseImageBtn;
    Button uploadBtn;
    ImageView addressImg;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask uploadTask;

    EditText addressEditText;

    Toolbar toolbar;

    EditText countOfStudios;

    ArrayList<Studio> studios;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);


        countOfStudios = findViewById(R.id.editCountStudios);

        studios = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание нового адреса");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        chooseImageBtn = findViewById(R.id.btnChooseFile);
        uploadBtn = findViewById(R.id.uploadBtn);
        addressImg = findViewById(R.id.imageAddress);
        addressEditText = findViewById(R.id.addressEditText);


        chooseImageBtn.setOnClickListener(view -> openFileChooser());
        uploadBtn.setOnClickListener(view -> {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(AddressActivity.this, "Просходит загрузка", Toast.LENGTH_SHORT).show();
            } else {
                if (!countOfStudios.getText().toString().equals("")&&!addressEditText.getText().toString().equals("")) {
                    try {
                        uploadFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(AddressActivity.this, "Заполните пустые поля", Toast.LENGTH_SHORT).show();
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

            Picasso.with(AddressActivity.this).load(imgUri).into(addressImg);
        }
    }


    private byte[] compressImg(Uri uri) throws IOException {
        InputStream imageStream;
        imageStream = getContentResolver().openInputStream(uri);

        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
        byte[] bitmapData = stream.toByteArray();
        stream.close();
        return bitmapData;
    }

    private void uploadFile() throws IOException {
        if (imgUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            uploadTask = fileReference.putBytes(compressImg(imgUri)).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(AddressActivity.this, "Загружено", Toast.LENGTH_SHORT).show();
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    for (int i = 0; i < Integer.parseInt(countOfStudios.getText().toString()); i++) {
                        studios.add(new Studio(i, "Студия " + (i + 1), "", ""));
                    }
                    Address upload = new Address(addressEditText.getText().toString().trim(), url, studios);
                    String uploadId = databaseReference.push().getKey();
                    assert uploadId != null;
                    databaseReference.child(uploadId).setValue(upload);
                });

            })
                    .addOnFailureListener(e ->
                            Toast.makeText(AddressActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show());
        }else{
            Toast.makeText(this, "Выберите файл", Toast.LENGTH_SHORT).show();
        }
        studios.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
