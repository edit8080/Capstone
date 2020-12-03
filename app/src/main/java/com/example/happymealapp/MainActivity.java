package com.example.happymealapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.happymealapp.ProfileActivity.userName;
import static com.example.happymealapp.ProfileActivity.profileURL;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    public static HashMap<String, String> favoriteList;
    public static HashMap<String, String> ingredientList;

    private Context mContext;
    private final String SAVE_MENU="FavoriteList";
    private final String SAVE_INGREDIENT="ingredientList";

    private AccessToken accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.animate().translationY(600).setDuration(1000).setStartDelay(4000);

        mContext=this;
        favoriteList = new HashMap<String, String>();
        ingredientList = new HashMap<String, String>();

        favoriteList.putAll(getMenuHashMapPref(SAVE_MENU));
        ingredientList.putAll(getMenuHashMapPref(SAVE_INGREDIENT));

        if(AccessToken.getCurrentAccessToken()!=null){
            Profile profile = Profile.getCurrentProfile();

            userName = Profile.getCurrentProfile().getName();
            profileURL = profile.getProfilePictureUri(150,150).toString();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,Scanner.class);
                        startActivity(intent);
            }
        }, 10000);
    }
    // SharedPreference 내용 fetch
    private HashMap<String,String> getMenuHashMapPref(String key) {
        HashMap<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = getSharedPreferences(key,MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}