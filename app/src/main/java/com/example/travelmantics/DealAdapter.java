package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{
    ArrayList<TravelDeals> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;
    public DealAdapter(){

        mFirebaseDatabase=FireBaseUtil.firebaseDatabase;
        mDatabase=FireBaseUtil.databaseReference;
       deals=FireBaseUtil.mDeals;

        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // TextView tvDeals=findViewById(R.id.tv_deals);
                TravelDeals td=dataSnapshot.getValue(TravelDeals.class);
                Log.d("Deal :", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size()-1);
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
        mDatabase.addChildEventListener(mChildEventListener);
    }






    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View itemView= LayoutInflater.from(context).inflate(R.layout.rv_row,parent,false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeals travelDeals=deals.get(position);
        holder.bind(travelDeals);

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle,tvDescription,tvPrice;
        ImageView imageView;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            tvDescription=itemView.findViewById(R.id.tvDescription);
            tvPrice=itemView.findViewById(R.id.tvPrice);
             imageView=itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeals travelDeals){
            tvTitle.setText(travelDeals.getTitle());
            tvDescription.setText(travelDeals.getDescription());
            tvPrice.setText(travelDeals.getPrice());
            showImage(travelDeals.getImageUrl());

        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Log.d("click", String.valueOf(position));
            TravelDeals selectedDeals=deals.get(position);
            Intent intent=new Intent(v.getContext(),MainActivity.class);
            intent.putExtra("deal",selectedDeals);
            v.getContext().startActivity(intent);
        }

        private void showImage(String url){
            if(url != null && !url.isEmpty()){
                Picasso.get().load(url).resize(160,160).centerCrop().into(imageView);
            }
        }
    }
}
