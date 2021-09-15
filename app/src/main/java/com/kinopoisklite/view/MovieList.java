package com.kinopoisklite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieListFragmentBinding;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.view.adapter.MovieListAdapter;
import com.kinopoisklite.viewModel.MovieListViewModel;

import java.util.List;

public class MovieList extends Fragment {
    private static final String TITLE = "Список фильмов";

    private MovieListFragmentBinding binding;

    private MovieListViewModel mViewModel;

    public static MovieList newInstance() {
        return new MovieList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieListFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.movieListView.setLayoutManager(new LinearLayoutManager(getContext()));
        FragmentActivity parent = requireActivity();
        if (parent.getActionBar() != null)
            parent.getActionBar().setTitle(TITLE);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_movieList_to_movieAdd);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mViewModel.deleteMovie(((MovieListAdapter)
                        binding.movieListView.getAdapter()).getMovies().get(position));
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }
}