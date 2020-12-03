package com.example.happymealapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.logging.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import android.support.v7.widget.RecyclerView;

import static com.example.happymealapp.ProfileActivity.userName;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Community.item> mDataset; //MainActivity에 item class를 정의해 놓았음

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 사용될 항목들 선언

        public TextView mName;
        public TextView mReview;
        public TextView mHashTag;
        public ImageView mPhoto;
        public ImageView mProfile;
        public TextView mUsername;
        public Button mDelete;
        public ViewHolder(View v) {
            super(v);

            mName = (TextView) v.findViewById(R.id.info_name);
            mReview = (TextView) v.findViewById(R.id.info_review);
            mHashTag = (TextView) v.findViewById(R.id.info_hashtag);
            mPhoto = (ImageView) v.findViewById(R.id.image_card);
            mProfile = (ImageView) v.findViewById(R.id.profile_image);
            mUsername = (TextView) v.findViewById(R.id.info_username);
            mDelete = (Button) v.findViewById(R.id.button_delete);
        }
    }

    // 생성자 - 넘어 오는 데이터파입에 유의해야 한다.
    public MyAdapter(ArrayList<Community.item> myDataset) {
        mDataset = myDataset;
    }


    //뷰홀더
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getName());
        holder.mReview.setText(mDataset.get(position).getReview()); //int를 가져온다는점 유의
        holder.mHashTag.setText(mDataset.get(position).getHashTag());
        holder.mUsername.setText(mDataset.get(position).getUsername());
        Picasso.get().load(mDataset.get(position).getProfile()).into(holder.mProfile);
        Picasso.get().load(mDataset.get(position).getPhoto()).into(holder.mPhoto);

        if(!userName.equals("Anonymous") && holder.mUsername.getText().equals(userName))
            holder.mDelete.setVisibility(View.VISIBLE);
        else
            holder.mDelete.setVisibility(View.INVISIBLE);

        holder.mDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DatabaseReference mDatabaseRef;
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
                Query test = mDatabaseRef.orderByChild("mReview").equalTo(String.valueOf(holder.mReview.getText()));
                test.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    userSnapshot.getRef().removeValue();
                                }
                                holder.itemView.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //throw databaseError.toException();
                                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                            //mDatabaseRef.child("uploads").orderByChild("mName").equalTo("kimbap");
                            //Picasso.get().load("").into(mImageView);
                            ;
                        });
            }
        });

        };


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}