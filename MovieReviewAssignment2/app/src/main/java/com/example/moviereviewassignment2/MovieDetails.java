package com.example.moviereviewassignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.BundleCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetails extends AppCompatActivity {

    MovieCast movieCast;
    String backdrop_path, poster_path, overview, title, rating;

    OkHttpClient client = new OkHttpClient();


    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView moviePosterIV, moviebackDropIV;
        TextView movieTitleTV, movieOverviewTV, movieRatingTV;
        RatingBar ratingBar;

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        title = extras.getString("title");
        overview = extras.getString("ov");
        poster_path = extras.getString("posterPath");
        backdrop_path = extras.getString("backdropPath");
        rating = extras.getString("rating");

        moviePosterIV = findViewById(R.id.moviePosterIV);
        moviebackDropIV = findViewById(R.id.movieBackDropIV);
        movieTitleTV = findViewById(R.id.movieTitleTV);
        movieOverviewTV = findViewById(R.id.overviewTV);
        movieRatingTV = findViewById(R.id.ratingTV);
        ratingBar = findViewById(R.id.ratingBarRB);


        movieTitleTV.setText(title);
        movieOverviewTV.setText(overview);
        movieRatingTV.setText(rating);
        float rating_cal = (float) ((Float.parseFloat(rating) / 2));

        ratingBar.setRating(rating_cal);

        String url = "https://image.tmdb.org/t/p/w500" + poster_path;
        String url2 = "https://image.tmdb.org/t/p/w500" + backdrop_path;
        Log.i("Rukon", url);
        Glide.with(MovieDetails.this)
                .load(url)
                .centerCrop()
                .into(moviePosterIV);


        Glide.with(MovieDetails.this)
                .load(url2)
                .centerCrop()
                .into(moviebackDropIV);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RecyclerView recyclerViewCast = findViewById(R.id.recyleViewCast);
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCast.setAdapter(new MyAdapterCast());

        try {
            String url3 = "https://api.themoviedb.org/3/movie/"+id+"/credits?api_key=3fa9058382669f72dcb18fb405b7a831";
            String data = run(url3);
            movieCast = new Gson().fromJson(data, MovieCast.class);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    class MyAdapterCast extends RecyclerView.Adapter<CastHolder>{

        @NonNull
        @Override
        public CastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MovieDetails.this).inflate(R.layout.cast_items, parent, false);
            CastHolder castHolder = new CastHolder(view);
            return castHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CastHolder holder, int position) {
            String url = "https://image.tmdb.org/t/p/w500" + movieCast.getCast().get(position).getProfilePath();
            Glide.with(getApplicationContext())
                    .load(url)
                    .centerCrop()
                    .into(holder.castImageIV);

            holder.castNameTV.setText(movieCast.getCast().get(position).getOriginalName());


        }

        @Override
        public int getItemCount() {
            return movieCast.getCast().size();
        }
    }

    class CastHolder extends RecyclerView.ViewHolder{

        ImageView castImageIV;
        TextView castNameTV;

        public CastHolder(@NonNull View itemView) {
            super(itemView);
            castImageIV = itemView.findViewById(R.id.castImageIV);
            castNameTV = itemView.findViewById(R.id.castNameTV);

        }
    }
}