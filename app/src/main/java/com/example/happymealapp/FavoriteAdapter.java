package com.example.happymealapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.happymealapp.MainActivity.favoriteList;
import static com.example.happymealapp.MainActivity.ingredientList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private ArrayList<String> menuName = null;
    private ArrayList<String> imageURL = null;
    private ArrayList<String> menuIngredient = null;

    // getItemCount() - 전체 데이터 갯수 리턴.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuText;
        TextView menuIngredientText;
        ImageView image_view;
        ImageButton favoritebtn;

        ViewHolder(View itemView) {
            super(itemView);
            menuText = (TextView) itemView.findViewById(R.id.menuName);
            menuIngredientText = (TextView) itemView.findViewById(R.id.menuIngredientText);
            image_view = (ImageView) itemView.findViewById(R.id.image_view);
            favoritebtn = (ImageButton) itemView.findViewById(R.id.favoriteBtn);
            favoritebtn.setSelected(true);
        }
    }

    FavoriteAdapter(ArrayList<String> menu, ArrayList<String> image, ArrayList<String> ingredient) throws MalformedURLException {
        menuName = menu;
        imageURL = image;
        menuIngredient = ingredient;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_favorite_sheet, parent, false);

        FavoriteAdapter.ViewHolder vh = new FavoriteAdapter.ViewHolder(view);

        return vh;
    }
    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position) {
        holder.menuText.setText(menuName.get(position));
        holder.menuIngredientText.setText(menuIngredient.get(position));
        Picasso.get().load(imageURL.get(position)).into(holder.image_view);

        holder.favoritebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.favoritebtn.setSelected(!holder.favoritebtn.isSelected());

                if (holder.favoritebtn.isSelected()) {
                    favoriteList.put(menuName.get(position), imageURL.get(position));
                    ingredientList.put(menuName.get(position), menuIngredient.get(position));
                } else {
                    favoriteList.remove(menuName.get(position));
                    ingredientList.remove(menuName.get(position));
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return menuName.size();
    }
}