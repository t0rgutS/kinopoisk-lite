package com.kinopoisklite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kinopoisklite.R;
import com.kinopoisklite.databinding.MovieAddFragmentBinding;
import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.view.adapter.AgeRatingAdapter;
import com.kinopoisklite.viewModel.MovieAddViewModel;

import java.util.List;

public class MovieAdd extends Fragment {
    private static final String TITLE = "Добавить новый фильм";

    private MovieAddFragmentBinding binding;

    private MovieAddViewModel mViewModel;

    public static MovieAdd newInstance() {
        return new MovieAdd();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MovieAddFragmentBinding.inflate(getLayoutInflater(), container, false);
        FragmentActivity parent = requireActivity();
        if (parent.getActionBar() != null)
            parent.getActionBar().setTitle(TITLE);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                ((AgeRating) binding.ageRating.getSelectedItem()));
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
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovieAddViewModel.class);
        mViewModel.getAgeRatings().observe(getViewLifecycleOwner(), (List<AgeRating> ageRatings) -> {
            binding.ageRating.setAdapter(new AgeRatingAdapter(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, ageRatings));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }

}