package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private static final int PICTUR_RESULT = 42;
    ImageView imageView;
    EditText txtTitle, txtDescription, txtPrice;
    TravelDeals deals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");
        txtTitle = findViewById(R.id.editText_title);
        txtDescription = findViewById(R.id.editText_description);
        txtPrice = findViewById(R.id.editText_price);


        imageView = findViewById(R.id.dealImageView);
        Intent intent = getIntent();
        TravelDeals deals = (TravelDeals) intent.getSerializableExtra("deal");
        if (deals == null) {
            deals = new TravelDeals();
        }
        this.deals = deals;
        txtTitle.setText(deals.getTitle());
        txtDescription.setText(deals.getDescription());
        txtPrice.setText(deals.getPrice());
        showImage(deals.getImageUrl());
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/jpeg");
            intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent1, "insert picture"), PICTUR_RESULT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTUR_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FireBaseUtil.mStorageRef.child((imageUri.getLastPathSegment()));

            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnSuccessListener(this, taskSnapshot -> {
        String url=taskSnapshot.getStorage().getDownloadUrl().toString();
        deals.setImageUrl(url);
        showImage(url);
            });


        }
    }

    public void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url).resize(width, width * 2 / 3).centerCrop().into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deals_menu, menu);

        if (FireBaseUtil.isAdmin) {
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem((R.id.save)).setVisible(true);
            enableEditText(true);
        } else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem((R.id.save)).setVisible(false);
            enableEditText(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveDeal();
                Toast.makeText(this, "saved", Toast.LENGTH_LONG);
                clean();
                backToList();
                return true;
            case R.id.delete:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG);
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void clean() {
        txtTitle.setText(" ");
        txtPrice.setText(" ");
        txtDescription.setText(" ");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deals.setTitle(txtTitle.getText().toString());
        deals.setDescription(txtDescription.getText().toString());
        deals.setPrice(txtPrice.getText().toString());
        if (deals.getId() == null) {
            mDatabaseReference.push().setValue(deals);
        } else {
            mDatabaseReference.child(deals.getId()).setValue(deals);
        }


    }

    private void deleteDeal() {
        if (deals == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_LONG).show();
        }
        mDatabaseReference.child(deals.getId()).removeValue();
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void enableEditText(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
    }


}
