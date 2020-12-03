package com.example.happymealapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;


import android.content.ContentResolver;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collection;

public class AugmentedReality extends AppCompatActivity {


    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;
    private ImageView mImageView;
    private Button youtubeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_reality);

        texture = new ExternalTexture();
        mImageView = findViewById(R.id.image_view);
        youtubeBtn = findViewById(R.id.youtube);
        youtubeBtn.setEnabled(false);

        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("video_screen.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));

                    renderable = modelRenderable;
                });

        arFragment = (CustomArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene = arFragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);

        //initialize and assign variables for bottomtabBar.

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set community selected.
        bottomNavigationView.setSelectedItemId(R.id.ar);

        //PerformItemSelectedListener.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.ar:
                        return true;

                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(),Scanner.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.community:
                        startActivity(new Intent(getApplicationContext(),Community.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0, 0);

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

    private void onUpdate(FrameTime frameTime) {

        String text_search;
        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image : augmentedImages) {

            if (image.getTrackingState() == TrackingState.TRACKING) {
                //검색부분
                if (!(text_search=image.getName()).isEmpty()) {
                    text_search=image.getName();

                    isImageDetected = true;

                    playVideo (text_search,image.createAnchor(image.getCenterPose()), image.getExtentX(),
                            image.getExtentZ());
                }
            }
        }
    }

    private void playVideo(String text_search,Anchor anchor, float extentX, float extentZ) {

        mediaPlayer = MediaPlayer.create(this, R.raw.video);

        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        AnchorNode anchorNode = new AnchorNode(anchor);


        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 1f, extentZ));

        scene.addChild(anchorNode);
        run(text_search);


        if(!mediaPlayer.isPlaying()) {
            scene.removeChild(anchorNode);
            mediaPlayer.release();

        }

    }
    public void run(String text_search){
        DatabaseReference mDatabaseRef;
        DatabaseReference mYoutubeRef;

        TextView textView = findViewById(R.id.translatedTv);
        ImageView imageView = findViewById(R.id.image_view);
        String[] reviews = new String[11];
        String[] img_reviews = new String[11];

        mYoutubeRef = FirebaseDatabase.getInstance().getReference("youtube");
        mYoutubeRef.orderByChild("mName").equalTo(text_search).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;

                        for(DataSnapshot userSnapshot: dataSnapshot.getChildren())
                        {
                            youtubeBtn.setEnabled(true);
                            youtubeBtn.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {
                                    String url = userSnapshot.child("mUrl").getValue().toString();
                                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
                                    Log.i("Video", "Video Playing....");
                                }
                            });


                            break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.orderByChild("mName").equalTo(text_search).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        textView.setText("menu recognized!");
                        for(DataSnapshot userSnapshot: dataSnapshot.getChildren())
                        {

                            reviews[count]=userSnapshot.child("mReview").getValue().toString();
                            img_reviews[count]=userSnapshot.child("mImageUrl").getValue().toString();
                            count++;
                            if(count>10) // 리뷰 10개 불러오기
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

        textView.post(new Runnable() {
            int i =0;
            public void run() {

                textView.setText(reviews[i]);

                Picasso.get().load(img_reviews[i]).into(mImageView);

                //Picasso.get().load(img_reviews[i]).into(imageView);
                i++;
                if (i >10)
                    i = 0;
                else if(reviews[i]==null)
                    i = 0;
                textView.postDelayed(this, 5000);
            }
        });
    }
}


