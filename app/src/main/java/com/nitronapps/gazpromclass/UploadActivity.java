package com.nitronapps.gazpromclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nitronapps.gazpromclass.MainActivity.APP_SETTINGS;
import static com.nitronapps.gazpromclass.MainActivity.BASIC_URL;

public class UploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static int GALLERY = 0, PHOTO = 1;
    private EditText titleEdit, contentEdit;
    private ImageView showResponse;
    private ProgressBar progressBar;
    private Button addImages, send;
    private LinearLayout layoutAttPhotos;
    private static final String IMAGE_TYPE = "image/*";
    private ArrayList<Uri> pathsToImages = new ArrayList<>();
    private SharedPreferences sp;
    private String name, mCurrentPhotoPath;
    private AlertDialog.Builder alertDialogBuilder;
    private final String[] points = new String[2];


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        titleEdit = (EditText) findViewById(R.id.editTextTitle);
        contentEdit = (EditText) findViewById(R.id.editTextContent);
        addImages = (Button) findViewById(R.id.buttonUpload);
        send = (Button) findViewById(R.id.buttonSend);
        layoutAttPhotos = (LinearLayout) findViewById(R.id.linearLayoutAttPhotos);
        progressBar = (ProgressBar) findViewById(R.id.progressBarUpload);
        showResponse = (ImageView) findViewById(R.id.imageViewResponse);


        sp = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        name = sp.getString("name", "Android User");
        points[0] = getResources().getString(R.string.camera);
        points[1] = getResources().getString(R.string.gallery);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!titleEdit.getText().toString().equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(100000, TimeUnit.SECONDS)
                            .writeTimeout(100000, TimeUnit.SECONDS)
                            .readTimeout(100000, TimeUnit.SECONDS)
                            .build();
                    OkHttpPostManager manager = new OkHttpPostManager();

                    manager.addText("title", titleEdit.getText().toString());
                    manager.addText("content", Html.toHtml(new SpannableString(contentEdit.getText())));
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


                            if (response.body().string().equals("Успешно! Ваш пост отправлен на модерацию.")) {
                                showResponse.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResponse.setImageResource(R.drawable.ok);
                                        showResponse.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                showResponse.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResponse.setImageResource(R.drawable.fail);
                                        showResponse.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                            try {
                                Thread.sleep(5000);
                                showResponse.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResponse.setVisibility(View.INVISIBLE);
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
                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.notificationNullTitle), Toast.LENGTH_SHORT).show();

            }
        });

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder = new AlertDialog.Builder(UploadActivity.this);

                alertDialogBuilder.setTitle(getResources().getString(R.string.chooseMethod));
                alertDialogBuilder.setItems(points, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 1:{
                                if(!hasPermissions()) requestPerms();

                                Intent intent = new Intent();
                                intent.setType(IMAGE_TYPE);
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(intent, GALLERY);
                                break;
                            }
                            case 0: {
                                if (!hasPermissions()) requestPerms();

                                Intent intent = new Intent();
                                try {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());

                                startActivityForResult(intent, PHOTO);
                                break;
                            }
                            }
                        }
                    }
            );
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



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
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            pathsToImages.add(data.getData());
            layoutAttPhotos.addView(getImageView(data.getData()));
            layoutAttPhotos.addView(getTextView());
        }

        if (requestCode == PHOTO && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            pathsToImages.add(Uri.fromFile(file));
            layoutAttPhotos.addView(getImageView(Uri.fromFile(file)));
            layoutAttPhotos.addView(getTextView());
        }
    }

    private boolean hasPermissions(){
        int res = 0;
        String[] permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, 123);
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



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

