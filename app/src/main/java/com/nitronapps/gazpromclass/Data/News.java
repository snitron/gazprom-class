package com.nitronapps.gazpromclass.Data;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.DrawFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class News implements Serializable {
    public String title;
    public String content;
    public String date;
    public String author;
    public String[] att_urls;
    public String photo_mini_url;
    public String[] thumbs_ids;
}
