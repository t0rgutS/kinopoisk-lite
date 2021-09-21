package com.kinopoisklite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kinopoisklite.repository.MovieDTOFactory;
import com.kinopoisklite.repository.dtoVersion;
import com.kinopoisklite.repository.ResourceManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ResourceManager.initRoom(getApplication());
        MovieDTOFactory.init(dtoVersion.ROOM);
    }
}
