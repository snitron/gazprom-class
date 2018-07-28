package com.nitronapps.gazpromclass.Util;

import com.nitronapps.gazpromclass.Data.News;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import okhttp3.ResponseBody;

public interface ServerAPI {
    @Headers("User-Agent: Nitron Apps Gazprom Class Http Connector")
    @GET("uploaderl.php")
    Call<ArrayList<News>> news(@Query("action") String action, @Query("group") int group);
}
