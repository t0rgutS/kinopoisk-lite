package com.kinopoisklite.viewModel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.utility.CoverProvider;

import java.io.FileNotFoundException;

import lombok.Setter;

public class CoverViewModel extends ViewModel {
    @Setter
    private String coverUri;

    @Setter
    private String coverContent;

    public Bitmap getCover(Activity parent) throws FileNotFoundException {
        return coverUri != null
                ? CoverProvider.getFromLocal(coverUri, parent)
                : CoverProvider.getFromServer(coverContent);
    }
}