package com.aplicafran.parejafinder;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS = "pareja_finder_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveEmail(String email) {
        preferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getDisplayName() {
        String email = getEmail();
        int atIndex = email.indexOf("@");
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return email;
    }

    public boolean isLoggedIn() {
        return !getEmail().trim().isEmpty();
    }

    public void saveAuthToken(String token) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token == null ? "" : token).apply();
    }

    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, "");
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_EMAIL)
                .remove(KEY_AUTH_TOKEN)
                .apply();
    }
}
