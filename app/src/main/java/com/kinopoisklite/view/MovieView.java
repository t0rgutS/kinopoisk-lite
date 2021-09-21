package com.kinopoisklite.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieFragmentBinding;
import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.view.adapter.AgeRatingAdapter;
import com.kinopoisklite.viewModel.MovieViewModel;

import java.util.List;

public class MovieView extends Fragment {
    private MovieFragmentBinding binding;

    private MovieViewModel mViewModel;

    private String coverUri = null;

    public static MovieView newInstance() {
        return new MovieView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieFragmentBinding.inflate(getLayoutInflater(), container, false);
        FragmentActivity parent = requireActivity();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(binding.title.getText().toString().isEmpty() ||
                        binding.releaseYear.getText().toString().isEmpty() ||
                        binding.duration.getText().toString().isEmpty())) {
                    try {
                        mViewModel.upsertMovie(binding.title.getText().toString(),
                                binding.releaseYear.getText().toString(),
                                binding.duration.getText().toString(),
                                binding.description.getText().toString(),
                                ((AgeRating) binding.ageRating.getSelectedItem()),
                                coverUri);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    Navigation.findNavController(v).popBackStack();
                } else
                    Toast.makeText(getContext(), "Заполните все обязательные поля!",
                            Toast.LENGTH_LONG).show();
            }
        };
        binding.fab.setOnClickListener(clickListener);
        binding.saveFab.setOnClickListener(clickListener);
        binding.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.getActivityResultRegistry().register("ImageLoader",
                        new ActivityResultContracts.OpenDocument(),
                        new ActivityResultCallback<Uri>() {
                            @Override
                            public void onActivityResult(Uri result) {
                                if (result != null) {
                                    parent.getApplicationContext().getContentResolver()
                                            .takePersistableUriPermission(result,
                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    coverUri = result.toString();
                                    try {
                                        binding.cover.setImageBitmap(
                                                BitmapFactory.decodeFileDescriptor(
                                                        parent.getApplicationContext()
                                                                .getContentResolver().
                                                                openFileDescriptor(
                                                                        Uri.parse(result.toString()), "r")
                                                                .getFileDescriptor()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else if (coverUri != null)
                                    coverUri = null;
                            }
                        }).launch(new String[]{"image/*"});
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        mViewModel.getAgeRatings().observe(getViewLifecycleOwner(), (List<AgeRating> ageRatings) -> {
            binding.ageRating.setAdapter(new AgeRatingAdapter(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, ageRatings));
            if (getArguments() != null) {
                Movie movie = new Gson()
                        .fromJson(getArguments()
                                .getString("movie"), Movie.class);
                mViewModel.setSavedMovie(movie);
                binding.ageRating.setSelection(((AgeRatingAdapter) binding.ageRating
                        .getAdapter()).getPosition(movie.getAgeRating()));
                binding.saveFab.setVisibility(View.VISIBLE);
                binding.fab.setVisibility(View.INVISIBLE);
                binding.title.setText(movie.getTitle());
                binding.releaseYear.setText(String.valueOf(movie.getReleaseYear()));
                binding.duration.setText(String.valueOf(movie.getDuration()));
                if (movie.getCoverUri() != null && !movie.getCoverUri().isEmpty()) {
                    try {
                        binding.cover.setImageBitmap(
                                BitmapFactory.decodeFileDescriptor(
                                        requireActivity().getApplicationContext()
                                                .getContentResolver().
                                                openFileDescriptor(
                                                        Uri.parse(movie.getCoverUri()), "r")
                                                .getFileDescriptor()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                binding.saveFab.setVisibility(View.INVISIBLE);
                binding.fab.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }
}