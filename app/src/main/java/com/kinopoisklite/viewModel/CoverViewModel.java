package com.kinopoisklite.viewModel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.io.FileNotFoundException;

import lombok.Setter;

public class CoverViewModel extends ViewModel {
    @Setter
    private String coverUri;

    public Bitmap getCover(Activity parent) throws FileNotFoundException {
        if (coverUri == null)
            return null;
        if (coverUri.isEmpty())
            return null;
        return BitmapFactory.decodeFileDescriptor(
                parent.getApplicationContext()
                        .getContentResolver().
                        openFileDescriptor(
                                Uri.parse(coverUri), "r")
                        .getFileDescriptor());
    }
}