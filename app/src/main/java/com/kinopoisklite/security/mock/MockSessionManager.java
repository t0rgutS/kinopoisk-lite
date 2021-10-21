package com.kinopoisklite.security.mock;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.security.Actions;
import com.kinopoisklite.security.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MockSessionManager implements SessionManager {
    private static final String SESSION_PREFERENCES = "SESSION_PREFERENCES";
    private static final String SESSION_USER_ID = "SESSION_USER_ID";

    private Context context;
    private User sessionUser;

    public MockSessionManager(Context context) {
        this.context = context;
    }

    public User getSessionUser() {
        if (sessionUser == null) {
            String id = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(SESSION_USER_ID, null);
            if (id != null)
                sessionUser = ResourceManager.getRepository().getUserById(id);
        }
        return sessionUser;
    }

    public List<Actions> getAllowedActions() {
        List<Actions> allowed = new ArrayList<>();
        User sessionUser = getSessionUser();
        if (sessionUser != null) {
            Integer accessLevel = sessionUser.getRole().getAccessLevel();
            allowed.add(Actions.ADD_TO_FAV);
            if (accessLevel > 1)
                allowed.add(Actions.UPDATE);
            if (accessLevel > 2) {
                allowed.add(Actions.CREATE);
                allowed.add(Actions.DELETE);
            }
        }
        return allowed;
    }

    public void setSessionUser(User user) {
        sessionUser = user;
        SharedPreferences.Editor editor = context.getSharedPreferences(SESSION_PREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putString(SESSION_USER_ID, user.getId());
        editor.apply();
    }

    public void logout() {
        if (sessionUser != null) {
            sessionUser = null;
            SharedPreferences.Editor editor = context.getSharedPreferences(SESSION_PREFERENCES,
                    Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
        }
    }
}
