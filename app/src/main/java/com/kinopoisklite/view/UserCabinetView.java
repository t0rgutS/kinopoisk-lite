package com.kinopoisklite.view;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kinopoisklite.R;
import com.kinopoisklite.databinding.UserCabinetFragmentBinding;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.User;
import com.kinopoisklite.view.adapter.MovieListAdapter;
import com.kinopoisklite.viewModel.UserCabinetViewModel;

import java.util.List;

public class UserCabinetView extends Fragment {
    private UserCabinetFragmentBinding binding;

    private UserCabinetViewModel mViewModel;

    public static UserCabinetView newInstance() {
        return new UserCabinetView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = UserCabinetFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.movieListView.setLayoutManager(new LinearLayoutManager(getContext()));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mViewModel.removeFavourite(((MovieListAdapter)
                        binding.movieListView.getAdapter()).getMovies().get(position));
            }
        }).attachToRecyclerView(binding.movieListView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserCabinetViewModel.class);
        User sessionUser = mViewModel.getSessionUser();
        if (sessionUser == null) {
            Navigation.findNavController(requireView()).navigate(R.id.action_userCabinet_to_loginFragment);
        } else {
            binding.loginView.setText(sessionUser.getLogin());
            binding.nameField.setText(sessionUser.getFirstName());
            binding.surnameField.setText(sessionUser.getLastName());
            if (sessionUser.getExternal()) {
                binding.nameField.setEnabled(false);
                binding.surnameField.setEnabled(false);
                binding.roleView.setText(sessionUser.getRole().getRoleName() + " (внешний)");
            } else {
                binding.nameField.setEnabled(true);
                binding.surnameField.setEnabled(true);
                binding.roleView.setText(sessionUser.getRole().getRoleName());
            }
            binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!binding.nameField.getText().toString().isEmpty()
                            && !binding.surnameField.getText().toString().isEmpty()) {
                        mViewModel.updateUser(binding.nameField.getText().toString(),
                                binding.surnameField.getText().toString());
                    } //else Toast.makeText(getContext(), "Заполните все обязательные поля!",
                    //    Toast.LENGTH_LONG).show();
                    Navigation.findNavController(v).popBackStack();
                }
            });
            mViewModel.getFavourites().observe(getViewLifecycleOwner(), (List<Movie> movies) -> {
                binding.movieListView.setAdapter(new MovieListAdapter(movies));
            });
            binding.logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.logout();
                    Navigation.findNavController(v).navigate(R.id.action_userCabinet_to_movieList);
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
}