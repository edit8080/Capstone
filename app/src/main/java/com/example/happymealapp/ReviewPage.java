package com.example.happymealapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static com.example.happymealapp.ProfileActivity.profileURL;
import static com.example.happymealapp.ProfileActivity.userName;

public class ReviewPage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    //private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    //private EditText mHashTag;
    private EditText mReview;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private String mHashTag;
    private Uri mImageUri;
    private CheckBox mCheckbox_1;
    private CheckBox mCheckbox_2;
    private CheckBox mCheckbox_3;
    private CheckBox mCheckbox_4;
    private CheckBox mCheckbox_5;
    private CheckBox mCheckbox_6;
    private CheckBox mCheckbox_7;
    private CheckBox mCheckbox_8;
    private EditText medit_hashtag;
    private Boolean check1 = Boolean.FALSE;
    private Boolean check2 = Boolean.FALSE;
    private Boolean check3 = Boolean.FALSE;
    private Boolean check4 = Boolean.FALSE;
    private Boolean check5 = Boolean.FALSE;
    private Boolean check6 = Boolean.FALSE;
    private Boolean check7 = Boolean.FALSE;
    private Boolean check8 = Boolean.FALSE;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewpage);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        //mTextViewShowUploads = findViewById(R.id.text_view_show_upload);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mReview = findViewById(R.id.review_textbox);
        //mHashTag = findViewById(R.id.hashtag_textbox);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mCheckbox_1 = findViewById(R.id.checkBox1);
        mCheckbox_2 = findViewById(R.id.checkBox2);
        mCheckbox_3 = findViewById(R.id.checkBox3);
        mCheckbox_4 = findViewById(R.id.checkBox4);
        mCheckbox_5 = findViewById(R.id.checkBox5);
        mCheckbox_6 = findViewById(R.id.checkBox6);
        mCheckbox_7 = findViewById(R.id.checkBox7);
        mCheckbox_8 = findViewById(R.id.checkBox8);
        medit_hashtag = findViewById(R.id.edit_hashtag);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mCheckbox_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check1=Boolean.TRUE;
                }
                else{
                    check1=Boolean.FALSE;
                }
            }
        });
        mCheckbox_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check2=Boolean.TRUE;
                }
                else{
                    check2=Boolean.FALSE;
                }
            }
        });

        mCheckbox_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check3=Boolean.TRUE;
                }
                else{
                    check3=Boolean.FALSE;
                }
            }
        });

        mCheckbox_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check4=Boolean.TRUE;
                }
                else{
                    check4=Boolean.FALSE;
                }
            }
        });

        mCheckbox_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check5=Boolean.TRUE;
                }
                else{
                    check5=Boolean.FALSE;
                }
            }
        });
        mCheckbox_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check6=Boolean.TRUE;
                }
                else{
                    check6=Boolean.FALSE;
                }
            }
        });
        mCheckbox_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check7=Boolean.TRUE;
                }
                else{
                    check7=Boolean.FALSE;
                }
            }
        });
        mCheckbox_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    check8=Boolean.TRUE;
                }
                else{
                    check8=Boolean.FALSE;
                }
            }
        });

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ReviewPage.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });


    };

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        String username = userName; // 로그인 값으로 연결 필요!!!
        String profile_img_url = profileURL; // 로그인 사진 값으로 연결 필요!!!

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setProgress(0);
                                        }
                                    }, 500);

                                    Toast.makeText(ReviewPage.this, "Upload successful", Toast.LENGTH_LONG).show();
                                    String hashTag = "";
                                    if(check1)
                                        hashTag=hashTag +"#Cow ";
                                    if(check2)
                                        hashTag= hashTag + "#Pig ";
                                    if(check3)
                                        hashTag= hashTag + "#Peanut ";
                                    if(check4)
                                        hashTag= hashTag + "#Gluten ";
                                    if(check5)
                                        hashTag= hashTag + "#Vegan ";
                                    if(check6)
                                        hashTag= hashTag + "#Spicy ";
                                    if(check7)
                                        hashTag= hashTag + "#Soya ";
                                    if(check8)
                                        hashTag= hashTag + "#Diet ";
                                    String[] customhash = medit_hashtag.getText().toString().split(",");
                                    if(customhash[0]!="") {
                                        for (String hash : customhash) {
                                            hashTag = hashTag + "#" + hash + " ";
                                        }
                                    }
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                            uri.toString(),mReview.getText().toString().trim(), hashTag, username, profile_img_url);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    //downloadUrl= uri.toString();
                                    //mEditTextFileName.setText(downloadUrl);
                                    startActivity(new Intent(ReviewPage.this, Community.class));
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReviewPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}
