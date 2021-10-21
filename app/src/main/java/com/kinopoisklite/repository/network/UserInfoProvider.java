package com.kinopoisklite.repository.network;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.util.Map;

public interface UserInfoProvider {
    LiveData<Map> getUserInfo(String token) throws IOException;
}
