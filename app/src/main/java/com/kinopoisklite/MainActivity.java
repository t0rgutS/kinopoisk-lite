package com.kinopoisklite;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.MovieDTOFactory;
import com.kinopoisklite.repository.dtoFactory.UserRequestFactory;
import com.kinopoisklite.repository.Version;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ResourceManager.init(getApplication(), Version.NETWORK);
        MovieDTOFactory.init(Version.ROOM);
        UserRequestFactory.init(Version.ROOM);
    }
}
