package com.kinopoisklite.security;

import com.kinopoisklite.model.User;

import java.util.List;

public interface SessionManager {
    User getSessionUser();
    void setSessionUser(User user);
    void logout();
    List<Actions> getAllowedActions();
}
