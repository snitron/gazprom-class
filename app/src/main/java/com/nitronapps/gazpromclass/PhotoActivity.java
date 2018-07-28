package com.nitronapps.gazpromclass;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nitronapps.gazpromclass.Util.ScalingImage;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = (ScalingImage) findViewById(R.id.imageView);
        Intent intent = getIntent();
        if (intent.hasExtra("isUploadActivity") && intent.getBooleanExtra("isUploadActivity", false))
            Picasso.get().load(new File(intent.getStringExtra("url"))).placeholder(R.drawable.gazprom_placeholder).into(imageView);
        else
            Picasso.get().load(intent.getStringExtra("url")).placeholder(R.drawable.gazprom_placeholder).into(imageView);
    }
}
