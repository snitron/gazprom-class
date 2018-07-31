package com.nitronapps.gazpromclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nitronapps.gazpromclass.Data.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView title, date, author, content, name;
    private LinearLayout photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        title = (TextView) findViewById(R.id.textViewTitle);
        date = (TextView) findViewById(R.id.textViewDate);
        author = (TextView) findViewById(R.id.textViewAuthor);
        content = (TextView) findViewById(R.id.textViewContent);
        photos = (LinearLayout) findViewById(R.id.galleryLayout);
        name = (TextView) findViewById(R.id.textViewName);
        Intent gotIntent = getIntent();
        ArrayList<News> news = (ArrayList<News>) gotIntent.getSerializableExtra("data");
        int position = gotIntent.getIntExtra("id", -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            content.setText(Html.fromHtml(news.get(position).content, Html.FROM_HTML_MODE_COMPACT));
        else
            content.setText(Html.fromHtml(news.get(position).content));

        title.setText(news.get(position).title);
        date.setText(news.get(position).date);
        author.setText(news.get(position).author);

        for (int i = 0; i < news.get(position).att_urls.length; i++) {
            photos.addView(getImageView(news.get(position).att_urls[i]));
            photos.addView(getTextView());
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNews);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_news);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_news);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private View getImageView(final String url) {
        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setMaxHeight(400);
        imageView.setMaxWidth(400);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.gazprom_placeholder);
        Picasso.get().load(url).placeholder(R.drawable.gazprom_placeholder).resize(400, 400).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, PhotoActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        return imageView;
    }

    private View getTextView() {
        final TextView textView = new TextView(getApplicationContext());
        textView.setText("   ");
        return textView;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(NewsActivity.this, UploadActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(NewsActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_news);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_news);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
