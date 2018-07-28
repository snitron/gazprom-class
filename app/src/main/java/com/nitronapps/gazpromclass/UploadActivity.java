package com.nitronapps.gazpromclass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitronapps.gazpromclass.Data.Post;
import com.nitronapps.gazpromclass.Util.OkHttpPostManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nitronapps.gazpromclass.MainActivity.APP_SETTINGS;
import static com.nitronapps.gazpromclass.MainActivity.BASIC_URL;

public class UploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText titleEdit, contentEdit;
    private ImageView showOk;
    private TextView fileCounter;
    private ProgressBar progressBar;
    private Button addImages, send;
    private MenuItem menuItem;
    private LinearLayout layoutAttPhotos;
    private static final String IMAGE_TYPE = "image/*";
    private static final int SELECT_MULTIPLE_PICTURE = 201;
    private ArrayList<Uri> pathsToImages = new ArrayList<>();
    private SharedPreferences sp;
    private String name;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        titleEdit = (EditText) findViewById(R.id.editTextTitle);
        contentEdit = (EditText) findViewById(R.id.editTextContent);
        addImages = (Button) findViewById(R.id.buttonUpload);
        send = (Button) findViewById(R.id.buttonSend);
        layoutAttPhotos = (LinearLayout) findViewById(R.id.linearLayoutAttPhotos);
        menuItem = (MenuItem) findViewById(R.id.nav_gallery);
        fileCounter = (TextView) findViewById(R.id.textViewFiles);
        progressBar = (ProgressBar) findViewById(R.id.progressBarUpload);
        showOk = (ImageView) findViewById(R.id.imageViewOK);


        sp = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        name = sp.getString("name", "Android User");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100000, TimeUnit.SECONDS)
                        .writeTimeout(100000, TimeUnit.SECONDS)
                        .readTimeout(100000, TimeUnit.SECONDS)
                        .build();
                OkHttpPostManager manager = new OkHttpPostManager();

                manager.addText("title", titleEdit.getText().toString());
                manager.addText("content", contentEdit.getText().toString());
                manager.addText("author", name);
                manager.addText("count", pathsToImages.size() + "");

                for (int i = 0; i < pathsToImages.size(); i++)
                    manager.addImage("picture" + i, new File(getPath(pathsToImages.get(i))));

                RequestBody requestBody = manager.finalizeRequest();
                final Request request = new Request.Builder()
                        .url(BASIC_URL + "uploaderl.php")
                        .addHeader("User-Agent", "Nitron Apps Gazprom Class Http Connector")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                        showOk.post(new Runnable() {
                            @Override
                            public void run() {
                                showOk.setVisibility(View.VISIBLE);
                            }
                        });

                        try {
                            Thread.sleep(5000);
                            showOk.post(new Runnable() {
                                @Override
                                public void run() {
                                    showOk.setVisibility(View.INVISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                });

                titleEdit.setText("");
                contentEdit.setText("");
                layoutAttPhotos.removeAllViews();
                pathsToImages.clear();
                Toast.makeText(getApplicationContext(), "Запрос отправлен. Ожидайте ответ.", Toast.LENGTH_SHORT).show();
            }
        });

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, SELECT_MULTIPLE_PICTURE);

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUpload);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_upload);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_upload);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            pathsToImages.add(data.getData());
            layoutAttPhotos.addView(getImageView(data.getData()));
            layoutAttPhotos.addView(getTextView());
        }
    }

    private View getTextView() {
        TextView textView = new TextView(getApplicationContext());
        textView.setText("   ");
        return textView;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private View getImageView(final Uri url) {
        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setMaxHeight(400);
        imageView.setMaxWidth(400);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(url).placeholder(R.drawable.gazprom_placeholder).resize(400, 400).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, PhotoActivity.class);
                intent.putExtra("url", getPath(url));
                intent.putExtra("isUploadActivity", true);
                startActivity(intent);
            }
        });

        return imageView;
    }

    private void onServerResponse(String response) {
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(UploadActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_upload);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_upload);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}

