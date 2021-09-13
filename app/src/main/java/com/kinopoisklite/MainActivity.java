package com.kinopoisklite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kinopoisklite.model.dto.MovieDTOFactory;
import com.kinopoisklite.model.dto.dtoVersion;
import com.kinopoisklite.repository.RepositoryManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RepositoryManager.initRoom(getApplication());
        MovieDTOFactory.init(dtoVersion.ROOM);
    }
}
