package com.example.happymealapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.SharedPreferences;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.happymealapp.MainActivity.favoriteList;
import static com.example.happymealapp.MainActivity.ingredientList;

public class Favorites extends AppCompatActivity {
    private Context mContext;
    private final String SAVE_MENU="FavoriteList";
    private final String SAVE_INGREDIENT="ingredientList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //initialize and assign variables for bottomtabBar.

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set favorites  selected.
        bottomNavigationView.setSelectedItemId(R.id.favorites);

        // 리사이클뷰에 올릴 자료 임시 지정.
        mContext=this;

        ArrayList<String> favoritemenu = new ArrayList<>();
        ArrayList<String> menuImageURL = new ArrayList<>();
        ArrayList<String> menuIngredient = new ArrayList<>();
        Iterator<String> keys = favoriteList.keySet().iterator();

        while(keys.hasNext()) {
            String menu=keys.next();
            favoritemenu.add(menu);
            menuImageURL.add(favoriteList.get(menu));
            menuIngredient.add(ingredientList.get(menu));
        }

        RecyclerView recyclerView = findViewById(R.id.main_recyclerview) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        FavoriteAdapter adapter = null;
        try {
            adapter = new FavoriteAdapter(favoritemenu, menuImageURL, menuIngredient);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);


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
    protected void onStop() {
        super.onStop();
        setMenuHashMapPref(SAVE_INGREDIENT, ingredientList);
        setMenuHashMapPref(SAVE_MENU, favoriteList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setMenuHashMapPref(SAVE_INGREDIENT, ingredientList);
        setMenuHashMapPref(SAVE_MENU, favoriteList);

    }

    // SharedPreference에 내용 저장
    private void setMenuHashMapPref(String key, HashMap<String,String> values) {
        SharedPreferences pSharedPref = getSharedPreferences(key,MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(values);
            String jsonString = jsonObject.toString();

            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).commit();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }
}


