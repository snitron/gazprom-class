package com.nitronapps.gazpromclass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nitronapps.gazpromclass.Data.News;
import com.nitronapps.gazpromclass.Util.ConnectivityReceiver;
import com.nitronapps.gazpromclass.Util.NewsAdapter;
import com.nitronapps.gazpromclass.Util.ServerAPI;
import com.nitronapps.gazpromclass.Util.SpaceItemDecoration;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String APP_SETTINGS = "settings";
    private SharedPreferences sp;

    private SharedPreferences.Editor editor;
    private boolean hasNullPieceOfNewsChecked = false, hasRefreshed;
    ArrayList<News> news;
    public final static String BASIC_URL = "http://gazpromclass.teachertools.ru/";
    ProgressBar progressBar;
    String result = new String();
    private TextView name;
    private MenuItem menuItem;
    private static MainActivity mInstance;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Gson gson = new Gson();
    private ArrayList<Integer> packsGot = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(1));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        menuItem = (MenuItem) findViewById(R.id.nav_camera);
        sp = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);
        editor = sp.edit();

        if (!sp.contains("hasName") || !sp.getBoolean("hasName", false)) {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASIC_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverAPI = retrofit.create(ServerAPI.class);
        if (sp.contains("cachedNews")) {
            ArrayList<News> newsArrayList = gson.fromJson(sp.getString("cachedNews", null), new TypeToken<ArrayList<News>>() {
            }.getType());
            NewsAdapter newsAdapter = new NewsAdapter(newsArrayList);
            recyclerView.setAdapter(newsAdapter);
            hasRefreshed = false;
        } else {
            refreshNews();
            hasRefreshed = true;
            serverAPI.news("getAll", 0).enqueue(new Callback<ArrayList<News>>() {
                @Override
                public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                    String news = gson.toJson(response.body(), new TypeToken<ArrayList<News>>() {
                    }.getType());
                    editor.putString("cachedNews", news);
                    editor.apply();
                }

                @Override
                public void onFailure(Call<ArrayList<News>> call, Throwable throwable) {

                }
            });
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    final int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && !hasNullPieceOfNewsChecked && hasRefreshed && !packsGot.contains(totalItemCount / 4) && totalItemCount >= 4) {
                        progressBar.setVisibility(View.VISIBLE);
                        final int packCount = layoutManager.getItemCount() / 4;
                        packsGot.add(packCount);
                        serverAPI.news("get", packCount).enqueue(new Callback<ArrayList<News>>() {
                            @Override
                            public void onResponse(Call<ArrayList<News>> call, retrofit2.Response<ArrayList<News>> response) {
                                NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
                                for (int i = 0; i < 4; i++) {
                                    if (response.body().get(i) != null && !hasNullPieceOfNewsChecked) {
                                        adapter.putOnePieceOfNews(response.body().get(i));
                                    } else {
                                        hasNullPieceOfNewsChecked = true;
                                        break;
                                    }

                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onFailure(Call<ArrayList<News>> call, Throwable throwable) {

                            }
                        });
                    }
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Callback<ArrayList<News>> callback = new Callback<ArrayList<News>>() {
                    @Override
                    public void onResponse(Call<ArrayList<News>> call, retrofit2.Response<ArrayList<News>> response) {
                        ArrayList<News> news = new ArrayList<>();
                        if (response.body().contains(null)) {
                            news.ensureCapacity(response.body().indexOf(null));
                            for (int i = 0; i < response.body().indexOf(null); i++)
                                news.add(response.body().get(i));
                        } else news = response.body();

                        NewsAdapter newsAdapter = new NewsAdapter(news);

                        recyclerView.setAdapter(newsAdapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        hasRefreshed = true;

                        if (response.body().contains(null))
                            hasNullPieceOfNewsChecked = true;
                        else
                            hasNullPieceOfNewsChecked = false;

                        packsGot.clear();
                        packsGot.add(0);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<News>> call, Throwable throwable) {
                        showFailure();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };

                Call<ArrayList<News>> newsCall = serverAPI.news("get", 0);
                packsGot.add(0);
                newsCall.enqueue(callback);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.gazprom_classic);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void refreshNews() {
        Callback<ArrayList<News>> callback = new Callback<ArrayList<News>>() {
            @Override
            public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                if (response.body().contains(null))
                    hasNullPieceOfNewsChecked = true;

                NewsAdapter newsAdapter = new NewsAdapter(response.body());
                recyclerView.setAdapter(newsAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                hasNullPieceOfNewsChecked = false;
            }

            @Override
            public void onFailure(Call<ArrayList<News>> call, Throwable throwable) {
                showFailure();
                progressBar.setVisibility(View.INVISIBLE);
            }
        };

        Call<ArrayList<News>> newsCall = serverAPI.news("get", 0);
        newsCall.enqueue(callback);
        packsGot.add(0);
        progressBar.setVisibility(View.VISIBLE);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void checkConnection() {
        if (!ConnectivityReceiver.isConnected()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.notificationConnectionError);
            alertDialogBuilder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkConnection();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.mInstance.onDestroy();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void showFailure() {
        Toast.makeText(this, R.string.notificationServerError, Toast.LENGTH_LONG).show();
    }

}
