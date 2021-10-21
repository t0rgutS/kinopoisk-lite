package com.kinopoisklite.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.kinopoisklite.R;
import com.kinopoisklite.viewModel.CoverViewModel;
import com.kinopoisklite.databinding.CoverFragmentBinding;

public class CoverFragment extends Fragment {
    private CoverFragmentBinding binding;

    private CoverViewModel mViewModel;

    public static CoverFragment newInstance() {
        return new CoverFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CoverFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
        binding.toCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_coverFragment_to_userCabinet);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CoverViewModel.class);
        if (getArguments() != null) {
            String coverUri = getArguments().getString("cover");
            mViewModel.setCoverUri(coverUri);
            try {
                Bitmap cover = mViewModel.getCover(requireActivity());
                if (cover != null)
                    binding.cover.setImageBitmap(cover);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}