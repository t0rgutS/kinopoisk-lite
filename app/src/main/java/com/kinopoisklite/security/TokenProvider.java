package com.kinopoisklite.security;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.util.Map;

public interface TokenProvider {
    LiveData<Map> getToken(String authCode) throws IOException;
}
