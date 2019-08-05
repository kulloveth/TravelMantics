package com.example.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireBaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeals> mDeals;
    private static ListActivity caller;
    public static boolean isAdmin;


    private static FireBaseUtil fireBaseUtil;
    private static int RC_SIGN_IN=1234;

    private FireBaseUtil() {
    }

    ;

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (fireBaseUtil == null) {
            fireBaseUtil = new FireBaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller =callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser()==null){
                        FireBaseUtil.signIn();
                    }else {
                        String userId=firebaseAuth.getUid();
                        checkAdmin(userId);
                    }

                    Toast.makeText(callerActivity.getBaseContext(),"welcome back",Toast.LENGTH_SHORT).show();
                    //signIn();
                }
            };
            connectStorage();


        }
        mDeals = new ArrayList<TravelDeals>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String userId) {
        FireBaseUtil.isAdmin=false;
        DatabaseReference ref=firebaseDatabase.getReference().child("administrators").child(userId);

        ChildEventListener listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FireBaseUtil.isAdmin=true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void connectStorage(){
        mFirebaseStorage=FirebaseStorage.getInstance();
        mStorageRef=mFirebaseStorage.getReference().child("deals_pictures");
    }
}
