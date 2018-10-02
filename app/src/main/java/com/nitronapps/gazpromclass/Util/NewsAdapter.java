package com.nitronapps.gazpromclass.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nitronapps.gazpromclass.NewsActivity;
import com.nitronapps.gazpromclass.Data.News;
import com.nitronapps.gazpromclass.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nitronapps.gazpromclass.MainActivity.BASIC_URL;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private ArrayList<News> news;
    private int newsCount;

    public void putOnePieceOfNews(News news) {
        this.news.add(news);
        newsCount++;
        this.notifyItemChanged(newsCount);
    }

    final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    public ArrayList<News> getNews() {
        return news;
    }

    public NewsAdapter(ArrayList<News> newsList) {
        news = newsList;
        newsCount = newsList.size();
    }

    @Override
    public void onViewRecycled(@NonNull NewsViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        NewsViewHolder newsViewHolder = new NewsViewHolder(v);

        return newsViewHolder;
    }


    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        if (news.get(position) != null) {
            if (!news.get(position).photo_mini_url.equals(""))
                Picasso.get().load(news.get(position).photo_mini_url).placeholder(R.drawable.gazprom_placeholder).into(holder.photo);
            else holder.photo.setImageResource(R.drawable.gazprom_placeholder);
            holder.title.setText(news.get(position).title);
            holder.date.setText(news.get(position).date);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), NewsActivity.class);
                    intent.putExtra("data", news);
                    intent.putExtra("id", position);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsCount;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView photo;
        TextView title;
        TextView date;

        public NewsViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            photo = (ImageView) itemView.findViewById(R.id.title_photo);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);

            cardView.setVisibility(View.VISIBLE);
        }
    }
}
