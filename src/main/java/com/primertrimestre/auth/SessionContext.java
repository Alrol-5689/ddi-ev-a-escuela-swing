package com.primertrimestre.auth;

import com.primertrimestre.model.User;

public class SessionContext {

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void clear() {
        currentUser = null;
    }
}
