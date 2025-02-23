package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

public class ProfilePictureActivity extends AppCompatActivity {
    ImageView profilePicture;
    String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        Utils.blackIconStatusBar(ProfilePictureActivity.this, R.color.black);
        profilePicture = findViewById(R.id.profilePicture);
        imageURI = getIntent().getStringExtra("imageURI");
        if (imageURI.equals("")||imageURI.isEmpty()){
            Glide.with(ProfilePictureActivity.this).load(R.mipmap.ic_profilepicture).into(profilePicture);
        }else {
            Glide.with(ProfilePictureActivity.this).load(imageURI).into(profilePicture);
        }
    }
}