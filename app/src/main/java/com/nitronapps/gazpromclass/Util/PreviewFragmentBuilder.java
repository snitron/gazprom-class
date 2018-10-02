package com.nitronapps.gazpromclass.Util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nitronapps.gazpromclass.R;
import com.squareup.picasso.Picasso;

public class PreviewFragmentBuilder extends Fragment {
    private int position = -1;
    private ImageView imageView;
    private TextView title, content;


    public static PreviewFragmentBuilder newInstance(int p){
        PreviewFragmentBuilder previewFragmentBuilder = new PreviewFragmentBuilder();
        Bundle bundle = new Bundle();
        bundle.putInt("position", p);
        previewFragmentBuilder.setArguments(bundle);
        return previewFragmentBuilder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        imageView = view.findViewById(R.id.imageViewPreview);
        title = view.findViewById(R.id.textViewPreviewTitle);

        Glide.with(this).load(R.drawable.gazprom).into(imageView);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setText(getResources().getStringArray(R.array.titles)[0]);

        return view;
    }


    public void changeFragmentData(int position){
        switch (position){
            case 0:{
                Glide.with(this).load(R.drawable.gazprom).into(imageView);
                title.setTextColor(Color.parseColor("#FFFFFF"));
                title.setText(getResources().getStringArray(R.array.titles)[0]);
                break;
            }

            case 1:{
                Glide.with(this).load(R.drawable.news).into(imageView);
                title.setTextColor(Color.parseColor("#000000"));
                title.setText(getResources().getStringArray(R.array.titles)[1]);
                break;
            }

            case 2:{
                Glide.with(this).load(R.drawable.photo).into(imageView);
                title.setTextColor(Color.parseColor("#000000"));
                title.setText(getResources().getStringArray(R.array.titles)[2]);
                break;
            }
        }
    }

}
