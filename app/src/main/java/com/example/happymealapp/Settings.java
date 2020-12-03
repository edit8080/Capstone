package com.example.happymealapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.json.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Settings extends AppCompatActivity{
    private static final int RC_SIGN_IN = 9001;

    //facebook login
    private LoginButton btn_facebook_login;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AccessToken.getCurrentAccessToken()!=null){
            startActivity(new Intent(Settings.this, ProfileActivity.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        //facebook login callback
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginCallback();

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        //버튼 조작
        btn_facebook_login = (LoginButton) findViewById(R.id.login_btn_facebook);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                try {
                    requestMe(loginResult.getAccessToken());
                    startActivity(new Intent(Settings.this, ProfileActivity.class));
                }
                catch(Exception e){
                    Log.d("Error", String.valueOf(e));
                }
            }
            @Override
            public void onCancel() {
                Log.e("Callback : ", "onCancel");
            }

            // 로그인 실패 시 호출
            @Override
            public void onError(FacebookException error) {
                Log.e("Callback :: ", "onError : " + error.getMessage());
            }
        });

        //initialize and assign variables for bottomtabBar.

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Settings  selected.
        bottomNavigationView.setSelectedItemId(R.id.settings);

        //PerformItemSelectedListener.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(),Scanner.class));
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
                        startActivity(new Intent(getApplicationContext(),Community.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.settings:
                        return true;

                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("Response", response.toString());
                        try {
                            String email = response.getJSONObject().getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}

