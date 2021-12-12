package com.kinopoisklite.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.kinopoisklite.repository.network.model.ServerCoverDTO;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Base64;

public class CoverProvider {

    public static Bitmap getFromLocal(String coverUri, Activity activity) throws FileNotFoundException {
        if (coverUri == null)
            return null;
        if (coverUri.isEmpty())
            return null;
        return BitmapFactory.decodeFileDescriptor(
                activity.getApplicationContext()
                        .getContentResolver().
                        openFileDescriptor(
                                Uri.parse(coverUri), "r")
                        .getFileDescriptor());
    }

    public static Bitmap getFromServer(String base64Image) {
        if (base64Image == null)
            return null;
        if (base64Image.isEmpty())
            return null;
        byte[] decodedImage = Base64.getDecoder().decode(base64Image);
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }
}