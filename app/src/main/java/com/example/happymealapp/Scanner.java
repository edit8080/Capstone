package com.example.happymealapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// import com.example.happymealapp.ImageSearch.SearchActivity;
import com.example.happymealapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import cz.msebera.android.httpclient.Header;
import static android.Manifest.permission.CAMERA;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.happymealapp.MainActivity.favoriteList;
import static com.example.happymealapp.MainActivity.ingredientList;
import static com.example.happymealapp.ProfileActivity.translateCode;

public class Scanner extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "Main Tag";
    private Mat matInput;
    private Mat matResult;

    private SurfaceView mSurfaceRoi;
    private SurfaceView mSurfaceRoiBorder;

    private Rect mRectRoi;

    private int mRoiWidth;
    private int mRoiHeight;
    private int mRoiX;
    private int mRoiY;

    // 인식 영역 Width와 Height 비율
    private double m_dWscale=(double)3/32;
    private double m_dHscale=(double)3/8;

    private Mat m_matRoi;
    private Bitmap bmp_result;
    private CameraBridgeViewBase mOpenCvCameraView;

    private TextView txtResult;
    private ImageView ivImage;
    private long mLastClickTime = 0;

    private android.widget.RelativeLayout.LayoutParams mRelativeParams;

    private OrientationEventListener mOrientEventListener;

    // 네이버 CLOVA OCR API 사용 전용 게이트웨이 및 인증키
    final String ocrApiGwUrl = "https://655ff427eea049c1b1bc6a0e86d57489.apigw.ntruss.com/custom/v1/5221/3ebd3b87789deae347077e7d20eafd61b6ec1fdb64ca0ae531f5ad7c6cca8c1f/general";
    final String ocrSecretKey = "eVZxa1hEVHl0cnNHbHZTUHJRWW5HR3RUd2xXa21nR0I=";

    protected static String ocrText="";

    private ConstraintLayout mCustomBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    private LinearLayout mHeaderLayout;
    private ImageView mHeaderImage;
    Button ShowScannerResultsButton;

    private ImageView[] image = new ImageView[3];
    private TextView hashtag;
    private TextView menutext;
    private TextView translatedtext;
    private String originalText;
    private String translatedText;
    private EditText inputToTranslate;
    private boolean connected;
    Translate translate;
    private String hashtag_max = "";

    SearchClient client;
    ArrayList<String> URL = new ArrayList<>();

    private ImageButton favoriteBtn;
    private String menuImageURL;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        // 풀스크린 만들기(상태바, 네비게이션 바 표시 안함)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        */
        setContentView(R.layout.activity_scanner);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)

        mSurfaceRoi = (SurfaceView) findViewById(R.id.surface_roi);
        mSurfaceRoiBorder = (SurfaceView) findViewById(R.id.surface_roi_border);

        txtResult = (TextView) findViewById(R.id.textView_ocr_result);
        //ivImage = (ImageView) findViewById(R.id.ivResult);

        mCustomBottomSheet = findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);
        ShowScannerResultsButton = findViewById(R.id.scanResultsButton);
        mHeaderLayout = findViewById(R.id.header_layout);
        mHeaderImage = findViewById(R.id.header_arrow);

        image[0] = findViewById(R.id.image_view);
        image[1] = findViewById(R.id.image_view2);
        image[2] = findViewById(R.id.image_view3);
        menutext = findViewById(R.id.menutext);
        translatedtext = findViewById(R.id.translatedtext);

        hashtag = findViewById(R.id.sliding_hashtag);
        favoriteBtn = findViewById(R.id.favoriteButton);

        client = new SearchClient();

        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    for(int i=0;i<3;i++)
                        image[i].setImageResource(0);

                    hashtag.setText("");
                    favoriteBtn.setSelected(false);
                }

            }
        });
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mHeaderImage.setRotation(slideOffset * 180);
            }
        });
        //Button tapbar menu implementations.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set favorites  selected.
        bottomNavigationView.setSelectedItemId(R.id.scanner);

        //PerformItemSelectedListener.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.scanner:
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
                        startActivity(new Intent(getApplicationContext(), Community.class));
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

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        // 가로, 세로 사이즈 획득
        mRoiWidth = (int) (matInput.size().width * m_dWscale);
        mRoiHeight = (int) (matInput.size().height * m_dHscale);

        // 사이즈로 중심에 맞는 X , Y 좌표값 계산
        mRoiX = (int) (matInput.size().width - mRoiWidth) / 2;
        mRoiY = (int) (matInput.size().height - mRoiHeight) / 2;

        // ROI 영역 생성
        mRectRoi = new Rect(mRoiX-400,mRoiY,mRoiWidth, mRoiHeight);

        // ROI 영역 흑백으로 전환
        m_matRoi = matInput.submat(mRectRoi);

        Imgproc.cvtColor(m_matRoi, m_matRoi, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.cvtColor(m_matRoi, m_matRoi, Imgproc.COLOR_GRAY2RGBA);

        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            mRelativeParams = new android.widget.RelativeLayout.LayoutParams(mRoiHeight +5,mRoiWidth +5);
                            mRelativeParams.setMargins(mRoiY,mRoiX,0,0);
                            mSurfaceRoiBorder.setLayoutParams(mRelativeParams);

                            mRelativeParams = new android.widget.RelativeLayout.LayoutParams(mRoiHeight -5,mRoiWidth -5);
                            mRelativeParams.setMargins(mRoiY +5,mRoiX +5,0,0);
                            mSurfaceRoi.setLayoutParams(mRelativeParams);
                        }
                    });
                }
            }).start();
        }
        catch(Exception e){
            Log.d(TAG+" Error ", String.valueOf(e));
        }

        m_matRoi.copyTo(matInput.submat(mRectRoi));

        return matInput;
    }

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    // 카메라 퍼미션 관련 메소드
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder( Scanner.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
    //카메라 퍼미션 관련 메소드 끝

    // 버튼 클릭 이벤트 메소드
    public void onClickButton(View v) {
        // 멀티 입력 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            //Start 버튼 클릭 시
            case R.id.scanResultsButton:
                // Crop 한 영역(Mat)을 Base64로 변환
                bmp_result=Bitmap.createBitmap(m_matRoi.cols(), m_matRoi.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(m_matRoi,bmp_result);

                bmp_result = BitmapImageEdit(bmp_result,200);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp_result.compress(Bitmap.CompressFormat.PNG,100,baos);

                byte[] buffer = baos.toByteArray();
                String encodedImage = Base64.encodeToString(buffer,Base64.DEFAULT);

                // OCR 실행
                ocrText="";
                OcrTask ocrTask = new OcrTask();
                ocrTask.execute(ocrApiGwUrl, ocrSecretKey,encodedImage);

                mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            ShowScannerResultsButton.setVisibility(View.INVISIBLE);
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            ShowScannerResultsButton.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!ocrText.isEmpty()) {
                            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                //naver ocr api 문자 건너받기
                                String menutext_string = ocrText;
                                menutext.setText(menutext_string);

                                if (checkInternetConnection()) {
                                    //If there is internet connection, get translate service and start translation:
                                    getTranslateService();
                                    translate(menutext_string, translateCode);
                                    favoriteBtn.setSelected(IsFavorite(translatedText));
                                } else {
                                    //If not, display "no connection" warning:
                                    translatedtext.setText("no connection to internet!");
                                }
                                getURL(ocrText, client);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int i = 0; i < URL.size(); i++) {
                                            if (i < 3)
                                                Picasso.get().load(URL.get(i)).into(image[i]);
                                            else
                                                break;
                                        }
                                        menuImageURL = URL.get(0);
                                        DatabaseReference mDatabaseRef;

                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
                                        String search_menu = translatedText; //번역된 언어로 입력!!! (영어)
                                        Log.d("Hash_Tag","Bef_Translate : "+translatedText);
                                        mDatabaseRef.orderByChild("mName").equalTo(search_menu).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        int count = 0;
                                                        String hashstring = null;

                                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                            if (userSnapshot.hasChild("mHashTag")) {
                                                                hashstring = userSnapshot.child("mHashTag").getValue().toString();
                                                                if (hashtag_max.length() < hashstring.length()) {
                                                                    hashtag_max = userSnapshot.child("mHashTag").getValue().toString();
                                                                }
                                                            }
                                                            //mEditTextFileName.setText("exists");
                                                            //Picasso.get().load(userSnapshot.child("mImageUrl").getValue().toString()).into(mImageView);
                                                            //mEditTextFileName.setText(userSnapshot.child("mImageUrl").getValue().toString());
                                                        }
                                                        Log.d("Hash_Tag","Translate : "+translatedText+" HashTag : "+hashtag_max);

                                                        hashtag.setText(hashtag_max);
                                                        hashtag_max = "";
                                                        //data will be available on dataSnapshot.getValue();
                                                        //mEditTextFileName.setText(dataSnapshot.getChildren("").toString());
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        throw databaseError.toException();
                                                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                                    }
                                                    //mDatabaseRef.child("uploads").orderByChild("mName").equalTo("kimbap");
                                                    //Picasso.get().load("").into(mImageView);
                                                    ;
                                                });
                                    }
                                }, 3000);

                            } else {
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Please Rescan Menu",Toast.LENGTH_LONG).show();
                            ocrText="";
                        }
                    }
                }, 2000);

                URL.clear();
                break;
            case R.id.favoriteButton:
                favoriteBtn.setSelected(!favoriteBtn.isSelected());

                // 즐겨찾기 추가
                if(favoriteBtn.isSelected()){
                    favoriteList.put(translatedText,menuImageURL);
                    if(hashtag_max.isEmpty())
                        hashtag_max="No Information";
                    ingredientList.put(translatedText, hashtag_max);
                    Toast.makeText(getApplicationContext(), "Add to Favorites",Toast.LENGTH_LONG).show();
                }
                else{
                    favoriteList.remove(translatedText);
                    ingredientList.remove(translatedText);
                    Toast.makeText(getApplicationContext(), "Delete From Favorites",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public Bitmap BitmapImageEdit(Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 너비
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;
        Bitmap retBitmap;

        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }
        retBitmap = Bitmap.createScaledBitmap(bmpSource, newWidth, newHeight, true);
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90);
        retBitmap = Bitmap.createBitmap(retBitmap, 0, 0, newWidth, newHeight, rotateMatrix, false);
        return retBitmap;
    }

    private boolean IsFavorite(String translatedText) {
        String text = "";

        if(favoriteList.get(translatedText) == null)
            return false;

        return true;
    }

    // OCR 클래스
    public class OcrTask extends AsyncTask<String, String, String> {
        @Override
        public String doInBackground(String... strings) {
            return OcrProc.main(strings[0], strings[1], strings[2]);
        }
        @Override
        protected void onPostExecute(String result) {
            ReturnThreadResult(result);
        }
    }
    // OCR 결과 출력 메소드(TextView에 표시)
    public void ReturnThreadResult(String result) {
        String rlt = result;
        try {
            JSONObject jsonObject = new JSONObject(rlt);
            JSONArray jsonArray  = jsonObject.getJSONArray("images");

            for (int i = 0; i < jsonArray.length(); i++ ){
                JSONArray jsonArray_fields  = jsonArray.getJSONObject(i).getJSONArray("fields");

                for (int j=0; j < jsonArray_fields.length(); j++ ){
                    String inferText = jsonArray_fields.getJSONObject(j).getString("inferText");
                    ocrText += inferText;
                    //ocrText += " ";
                }
            }
            txtResult.setText(ocrText);

        } catch (Exception e){

        }
    }
    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {
            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void translate(String menu_text, String language) {

        Translation translation = translate.translate(menu_text, Translate.TranslateOption.targetLanguage(language), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();
        translatedtext.setText(translatedText);
    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
    public ArrayList<String> getURL(String ocrText, SearchClient client){
        ImageFilter imageFilter = new ImageFilter();

        client.getSearch(ocrText, 1, imageFilter, this, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray imageJsonResults;
                            if (response != null) {
                                imageJsonResults = response.getJSONArray("items");
                                URL.addAll(ImageResult.fromJSONArray(imageJsonResults));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                }
        );
        return URL;
    }

}
