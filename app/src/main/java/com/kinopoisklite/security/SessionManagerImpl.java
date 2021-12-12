package com.kinopoisklite.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class SessionManagerImpl implements SessionManager {
    private static final String SESSION_PREFERENCES = "SESSION_PREFERENCES";
    private static final String SESSION_USER_ID = "SESSION_USER_ID";

    private Context context;
    private User sessionUser;

    public SessionManagerImpl(Context context) {
        this.context = context;
    }

    public User getSessionUser() {
        if (sessionUser == null) {
            String id = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(SESSION_USER_ID, null);
            if (id != null) {
                sessionUser = ResourceManager.getRepository().getUserById(id);
                if (sessionUser != null) {
                    Token token = ResourceManager.getRepository().getTokenByUserId(id);
                    sessionUser.setToken(token);
                }
            }
        }
        return sessionUser;
    }

    public List<Actions> getAllowedActions() {
        List<Actions> allowed = new ArrayList<>();
        User sessionUser = getSessionUser();
        if (sessionUser != null) {
            switch (sessionUser.getRole()) {
                case ROLE_ADMIN: {
                    allowed.add(Actions.CREATE);
                    allowed.add(Actions.DELETE);
                }
                case ROLE_MODER:
                    allowed.add(Actions.UPDATE);
                case ROLE_USER:
                    allowed.add(Actions.ADD_TO_FAV);
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
