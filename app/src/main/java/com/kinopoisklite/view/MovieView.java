package com.kinopoisklite.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kinopoisklite.MainActivity;
import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieFragmentBinding;
import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.network.model.ServerCoverDTO;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.security.Actions;
import com.kinopoisklite.view.adapter.AgeRatingAdapter;
import com.kinopoisklite.viewModel.MovieViewModel;

import java.util.List;

public class MovieView extends Fragment {
    private MovieFragmentBinding binding;

    private MovieViewModel mViewModel;

    private String coverUri = null;
    private Bitmap currentCover = null;
    private ActivityResultLauncher<String[]> coverLauncher;
    private PopupMenu coverPopupMenu;

    public static MovieView newInstance() {
        return new MovieView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieFragmentBinding.inflate(getLayoutInflater(), container, false);
        setCoverLauncher();
        setCoverPopup(binding.cover);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ResourceManager.getSessionManager().getAllowedActions().contains(Actions.UPDATE)) {
                    try {
                        mViewModel.saveMovie(binding.title.getText().toString(),
                                binding.releaseYear.getText().toString(),
                                binding.duration.getText().toString(),
                                binding.genre.getText().toString(),
                                binding.country.getText().toString(),
                                binding.description.getText().toString(),
                                ((AgeRating) binding.ageRating.getSelectedItem()),
                                coverUri);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                Navigation.findNavController(v).popBackStack();
            }
        });
        binding.toCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_movie_to_userCabinet);
            }
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(binding.title.getText().toString().isEmpty() ||
                        binding.releaseYear.getText().toString().isEmpty() ||
                        binding.duration.getText().toString().isEmpty()) ||
                        binding.genre.getText().toString().isEmpty() ||
                        binding.country.getText().toString().isEmpty()
                ) {
                    try {
                        mViewModel.addMovie(binding.title.getText().toString(),
                                binding.releaseYear.getText().toString(),
                                binding.duration.getText().toString(),
                                binding.genre.getText().toString(),
                                binding.country.getText().toString(),
                                binding.description.getText().toString(),
                                ((AgeRating) binding.ageRating.getSelectedItem()),
                                coverUri);
                        Navigation.findNavController(v).popBackStack();
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getContext(), "Заполните все обязательные поля!",
                            Toast.LENGTH_LONG).show();
            }
        });
        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mViewModel.addToFav();
                    binding.fav.setVisibility(View.INVISIBLE);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
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
            Movie movie = mViewModel.getSavedMovie();
            if (movie == null) {
                if (getArguments() != null)
                    movie = ResourceManager.getGson()
                            .fromJson(getArguments()
                                    .getString("movie"), Movie.class);
            }
            if (movie != null) {
                mViewModel.setSavedMovie(movie);
                binding.ageRating.setSelection(((AgeRatingAdapter) binding.ageRating
                        .getAdapter()).getPosition(movie.getAgeRating()));
                binding.fab.setVisibility(View.INVISIBLE);
                if (mViewModel.canAddToFav())
                    binding.fav.setVisibility(View.VISIBLE);
                binding.title.setText(movie.getTitle());
                binding.releaseYear.setText(String.valueOf(movie.getReleaseYear()));
                binding.duration.setText(String.valueOf(movie.getDuration()));
                binding.genre.setText(movie.getGenre());
                binding.country.setText(movie.getCountry());
                // if (movie.getCoverUri() != null && !movie.getCoverUri().isEmpty()) {
                try {
                    coverUri = movie.getCoverUri();
                    if (coverUri != null)
                        currentCover = mViewModel.getCover(requireActivity(), coverUri);
                    else if (getArguments().getString("cover") != null) {
                        ServerCoverDTO coverDTO = ResourceManager.getGson()
                                .fromJson(getArguments().getString("cover"),
                                        ServerCoverDTO.class);
                        currentCover = mViewModel.getCover(coverDTO.getContent());
                    }
                    if (currentCover != null)
                        binding.cover.setImageBitmap(currentCover);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                //   }
                binding.toolbar.setTitle("Фильм");
            } else {
                binding.fab.setVisibility(View.VISIBLE);
                binding.fav.setVisibility(View.INVISIBLE);
                binding.toolbar.setTitle("Добавить новый фильм");
            }
        });
        mViewModel.getGenres().observe(getViewLifecycleOwner(),
                (List<String> genres) -> {
                    ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, genres);
                    genreAdapter.getFilter().filter(null);
                    binding.genre.setAdapter(genreAdapter);
                });
        mViewModel.getCountries().observe(getViewLifecycleOwner(),
                (List<String> countries) -> {
                    ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, countries);
                    countryAdapter.getFilter().filter(null);
                    binding.country.setAdapter(countryAdapter);

                });
        if (!mViewModel.getAllowedActions().contains(Actions.UPDATE)) {
            binding.title.setEnabled(false);
            binding.releaseYear.setEnabled(false);
            binding.duration.setEnabled(false);
            binding.genre.setEnabled(false);
            binding.country.setEnabled(false);
            binding.description.setEnabled(false);
            binding.ageRating.setEnabled(false);
        } else {
            binding.title.setEnabled(true);
            binding.releaseYear.setEnabled(true);
            binding.duration.setEnabled(true);
            binding.genre.setEnabled(true);
            binding.country.setEnabled(true);
            binding.description.setEnabled(true);
            binding.ageRating.setEnabled(true);
            binding.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentCover == null) {
                        coverLauncher.launch(new String[]{"image/*"});
                    } else {
                        coverPopupMenu.show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }

    private void setCoverLauncher() {
        MainActivity parent = (MainActivity) requireActivity();
        coverLauncher = parent.getActivityResultRegistry().register("ImageLoader",
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
                                Bitmap cover = mViewModel.getCover(parent, result.toString());
                                if (cover != null) {
                                    binding.cover.setImageBitmap(cover);
                                    currentCover = cover;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (coverUri != null) {
                            coverUri = null;
                            currentCover = null;
                        }
                    }
                });
    }

    private void setCoverPopup(View v) {
        coverPopupMenu = new PopupMenu(requireContext(), v);
        coverPopupMenu.inflate(R.menu.cover_popup);
        coverPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open: {
                        try {
                            mViewModel.saveMovie(binding.title.getText().toString(),
                                    binding.releaseYear.getText().toString(),
                                    binding.duration.getText().toString(),
                                    binding.genre.getText().toString(),
                                    binding.country.getText().toString(),
                                    binding.description.getText().toString(),
                                    ((AgeRating) binding.ageRating.getSelectedItem()),
                                    coverUri);
                            Bundle bundle = new Bundle();
                            if (coverUri != null)
                                bundle.putString("coverUri", coverUri);
                            else if (getArguments() != null)
                                if (getArguments().getString("cover") != null)
                                    bundle.putString("coverContent",
                                            ResourceManager.getGson()
                                                    .fromJson(getArguments()
                                                                    .getString("cover"),
                                                            ServerCoverDTO.class).getContent());
                            Navigation.findNavController(v).navigate(R.id.action_movie_to_coverFragment, bundle);
                            return true;
                        } catch (PersistenceException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                    case R.id.delete: {
                        coverUri = null;
                        binding.cover.setImageResource(android.R.drawable.ic_menu_report_image);
                        return true;
                    }
                    case R.id.change: {
                        coverLauncher.launch(new String[]{"image/*"});
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
    }
}