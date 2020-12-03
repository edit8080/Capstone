package com.example.happymealapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Community extends AppCompatActivity {

    Button reviewBtn;
    private TextView textcom;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<item> itemArrayList;

    public void onClickButton(View v) {
        Boolean login = Boolean.TRUE;

        if (login) {
            Intent intent = new Intent();
            intent.setClass(this, ReviewPage.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Please login first.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        reviewBtn = findViewById(R.id.writeReview);
        DatabaseReference mDatabaseRef;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);//옵션
        //Linear layout manager 사용
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemArrayList = new ArrayList<>();

        //ArrayList에 값 추가하기$$
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        String name;
                        String hashTag = null;
                        String photo_url;
                        String review_text;
                        String username=null;
                        String profile_img=null;
                        for(DataSnapshot userSnapshot: dataSnapshot.getChildren())
                        {

                            name=userSnapshot.child("mName").getValue().toString();
                            review_text=userSnapshot.child("mReview").getValue().toString();
                            if(userSnapshot.hasChild("mHashTag")) {
                                hashTag = userSnapshot.child("mHashTag").getValue().toString();
                            }
                            photo_url=userSnapshot.child("mImageUrl").getValue().toString();
                            if(userSnapshot.hasChild("mUser")) {
                                username = userSnapshot.child("mUser").getValue().toString();
                            }
                            if(userSnapshot.hasChild("mProfile_img_url")) {
                                profile_img = userSnapshot.child("mProfile_img_url").getValue().toString();
                            }
                            itemArrayList.add(new item(name,review_text,hashTag, photo_url,username,profile_img));
                            count++;

                        }        //어답터 세팅
                        mAdapter = new MyAdapter(itemArrayList); //스트링 배열 데이터 인자로
                        mRecyclerView.setAdapter(mAdapter);
                        count=0;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //throw databaseError.toException();
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });







        //initialize and assign variables for bottomtabBar.

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set community selected.
        bottomNavigationView.setSelectedItemId(R.id.community);

        //PerformItemSelectedListener.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(), Scanner.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.ar:
                        startActivity(new Intent(getApplicationContext(), AugmentedReality.class));
                        overridePendingTransition(0, 0);
                        return true;


                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.community:
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;

                }


                return false;
            }
        });
    }


    public class item {
        String name;
        String review;
        String hashtag;
        String photo;
        String username;
        String profile;

        public item(String name, String review, String hashtag, String photo,String username, String profile) {
            this.name = name;
            this.review = review;
            this.hashtag = hashtag;
            this.photo = photo;
            this.username = username;
            this.profile = profile;
        }

        public String getName() {
            return name;
        }

        public String getReview() {
            return review;
        }

        public String getHashTag() {
            return hashtag;
        }

        public String getPhoto() {
            return photo;
        }

        public String getProfile(){return profile;}

        public String getUsername(){return username;}
    }
}



