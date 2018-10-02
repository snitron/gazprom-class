package com.nitronapps.gazpromclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.nitronapps.gazpromclass.Util.PreviewFragmentBuilder;




public class PreviewActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Button next, prev;
    private PreviewFragmentBuilder inst1;
    private int count = 0;
    private ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);


        next = findViewById(R.id.buttonPreviewNext);
        prev = findViewById(R.id.buttonPreviewPrev);

        fragmentManager = getSupportFragmentManager();
        inst1 = PreviewFragmentBuilder.newInstance(0);
        layout = findViewById(R.id.constraintLayoutPreview);

        fragmentManager.beginTransaction()
                .add(R.id.frame_layout, inst1)
                .commit();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 2){
                    finish();
                    startActivity(new Intent(PreviewActivity.this, RegistrationActivity.class));
                }

                switch(count){
                    case 0:{
                inst1.changeFragmentData(1);
                layout.setBackgroundColor(getResources().getColor(R.color.news_preview_color));
                count += 1;
                    break;}
                    case 1:
                        {
                        inst1.changeFragmentData(2);
                        layout.setBackgroundColor(getResources().getColor(R.color.photo_preview_color));
                        count += 1;
                        break;}

                }

                if(count > 0)
                    prev.setVisibility(View.VISIBLE);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(count){
                    case 1:{
                      inst1.changeFragmentData(0);
                        layout.setBackgroundColor(getResources().getColor(R.color.gazprom_classic));
                        count -= 1;
                        break;}
                    case 2:
                    {
                       inst1.changeFragmentData(1);
                        layout.setBackgroundColor(getResources().getColor(R.color.news_preview_color));
                        count -= 1;
                        break;}

                }
                if(count == 0) prev.setVisibility(View.INVISIBLE);
            }
        });

        }

    @Override
    public void onBackPressed() {

    }
}
