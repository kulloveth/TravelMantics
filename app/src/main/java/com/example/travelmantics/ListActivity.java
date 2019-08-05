package com.example.travelmantics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList<TravelDeals> deals;
    private FirebaseDatabase mFirebaseDatabase;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu,menu);
        MenuItem insertMenu=menu.findItem(R.id.insert);
        if (FireBaseUtil.isAdmin==true){
            insertMenu.setVisible(true);
        }else {
            insertMenu.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.insert:
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                FireBaseUtil.attachListener();
                            }
                        });
                FireBaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FireBaseUtil.openFbReference("traveldeals",this);
        RecyclerView recyclerView = findViewById(R.id.rvDeals);
        final DealAdapter dealAdapter = new DealAdapter();
        recyclerView.setAdapter(dealAdapter);
        linearLayoutManager =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        FireBaseUtil.attachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
