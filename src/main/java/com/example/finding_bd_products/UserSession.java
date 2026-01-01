package com.example.finding_bd_products;

public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private boolean isLoggedIn;

    private UserSession() {
        this.isLoggedIn = false;
        this.currentUser = null;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
}
