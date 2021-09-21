package com.kinopoisklite.view.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieListElementBinding;
import com.kinopoisklite.model.Movie;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private List<Movie> movies;

    public MovieListAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieListElementBinding binding = MovieListElementBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        if (movie != null) {
            holder.binding.titleView.setText(movie.getTitle());
            holder.binding.releaseYearView.setText("Год выхода: "
                    + String.valueOf(movie.getReleaseYear()));
            holder.binding.durationView.setText("Продолжительность: " +
                    String.valueOf(movie.getDuration()) + " минут");
            holder.binding.ageRatingView.setText("Возрастной рейтинг: "
                    + movie.getAgeRating().getRatingCategory());
            holder.binding.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("movie", new Gson().toJson(movie));
                    Navigation.findNavController(v)
                            .navigate(R.id.action_movieList_to_movie, bundle);
                }
            });
        }
    }

    public List<Movie> getMovies() {
        return movies;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private MovieListElementBinding binding;

        public MovieViewHolder(MovieListElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
