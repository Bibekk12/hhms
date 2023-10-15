package com.cqu.hhms.model;

public class Auth {

    // Instance variable to hold the single instance of the Auth class.
    private static Auth instance = null;

    // Variable to store the authenticated user.
    private User authUser;

    // Private constructor to prevent creating more than one instance.
    private Auth() {
    }

    // Public method to provide access to the instance.
    public static Auth getInstance() {
        if (instance == null) {
            instance = new Auth();
        }
        return instance;
    }

    // Method to set the authenticated user.
    public void setAuthUser(User user) {
        this.authUser = user;
    }

    // Method to get the authenticated user.
    public User getAuthUser() {
        return this.authUser;
    }
    

    // Optional: Method to clear the authenticated user (e.g., during logout).
    public void clearAuthUser() {
        this.authUser = null;
    }
}
