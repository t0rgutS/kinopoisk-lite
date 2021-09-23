package com.kinopoisklite.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kinopoisklite.MainActivity;
import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieFragmentBinding;
import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.view.adapter.AgeRatingAdapter;
import com.kinopoisklite.viewModel.MovieViewModel;

import java.util.List;

public class MovieView extends Fragment {
    private MovieFragmentBinding binding;

    private MovieViewModel mViewModel;

    private String coverUri = null;
    private ActivityResultLauncher<String[]> coverLauncher;
    private PopupMenu coverPopupMenu;

    public static MovieView newInstance() {
        return new MovieView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieFragmentBinding.inflate(getLayoutInflater(), container, false);
        FragmentActivity parent = requireActivity();
        setCoverLauncher();
        setCoverPopup(binding.cover);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mViewModel.saveMovie(binding.title.getText().toString(),
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
            }
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(binding.title.getText().toString().isEmpty() ||
                        binding.releaseYear.getText().toString().isEmpty() ||
                        binding.duration.getText().toString().isEmpty())) {
                    try {
                        mViewModel.addMovie(binding.title.getText().toString(),
                                binding.releaseYear.getText().toString(),
                                binding.duration.getText().toString(),
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
        binding.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coverUri == null) {
                    coverLauncher.launch(new String[]{"image/*"});
                } else {
                    coverPopupMenu.show();
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
                binding.title.setText(movie.getTitle());
                binding.releaseYear.setText(String.valueOf(movie.getReleaseYear()));
                binding.duration.setText(String.valueOf(movie.getDuration()));
                if (movie.getCoverUri() != null && !movie.getCoverUri().isEmpty()) {
                    coverUri = movie.getCoverUri();
                    try {
                        Bitmap cover = mViewModel.displayCover(requireActivity(), coverUri);
                        if (cover != null)
                            binding.cover.setImageBitmap(cover);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                binding.toolbar.setTitle("Фильм");
            } else {
                binding.fab.setVisibility(View.VISIBLE);
                binding.toolbar.setTitle("Добавить новый фильм");
            }
        });
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
                                Bitmap cover = mViewModel.displayCover(parent, result.toString());
                                if (cover != null)
                                    binding.cover.setImageBitmap(cover);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (coverUri != null)
                            coverUri = null;
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
                                    binding.description.getText().toString(),
                                    ((AgeRating) binding.ageRating.getSelectedItem()),
                                    coverUri);
                            Bundle bundle = new Bundle();
                            bundle.putString("cover", coverUri);
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