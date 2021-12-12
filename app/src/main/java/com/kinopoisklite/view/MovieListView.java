package com.kinopoisklite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieListFragmentBinding;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.security.Actions;
import com.kinopoisklite.view.adapter.MovieListAdapter;
import com.kinopoisklite.viewModel.MovieListViewModel;

import java.util.List;

public class MovieListView extends Fragment {
    private MovieListFragmentBinding binding;

    private MovieListViewModel mViewModel;

    public static MovieListView newInstance() {
        return new MovieListView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieListFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.movieListView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.toCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_movieList_to_userCabinet);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        mViewModel.getAllMovies().observe(getViewLifecycleOwner(), (List<Movie> movies) -> {
            binding.movieListView.setAdapter(new MovieListAdapter(movies));
        });
        if (mViewModel.getAllowedActions().contains(Actions.CREATE))
            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(R.id.action_movieList_to_movie);
                }
            });
        else
            binding.fab.setVisibility(View.INVISIBLE);
        if (mViewModel.getAllowedActions().contains(Actions.DELETE))
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    List<Movie> movies = ((MovieListAdapter)
                            binding.movieListView.getAdapter()).getMovies();
                    mViewModel.deleteMovie(movies.get(position));
                    movies.remove(position);
                    binding.movieListView.getAdapter().notifyDataSetChanged();
                }
            }).attachToRecyclerView(binding.movieListView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }
}