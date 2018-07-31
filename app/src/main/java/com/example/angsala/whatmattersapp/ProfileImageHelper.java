package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

import com.parse.ParseFile;

public class ProfileImageHelper {
    ParseFile img;
    String username;
    String imgUrl;
    Context context;
    Handler myHandler = new Handler();




    public void getImage(String username, ImageView imageView) {
        if (username.equalsIgnoreCase("FakeUser")) {
            Drawable drawable = context.getResources().getDrawable((R.drawable.anotherlogo));
            imageView.setImageDrawable(drawable);

        }
    }



}
