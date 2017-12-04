package com.langram.utils;

import java.util.HashMap;
import java.util.Locale;

public class Settings {

    private HashMap<String, String> params = new HashMap<>();
    private Locale locale = new Locale("fr", "FR");
    private String username = "";
    private static Settings instance = null;

    // Get instance
    public static Settings getInstance() {
        if(instance == null)
            instance = new Settings();
        return instance;
    }

    // Init locale and params
    Settings() {
        Locale.setDefault(locale);
        System.setProperty("java.net.preferIPv4Stack" , "true");
        params.put("AppName", "Langram");
    }

    // @return String Application Name
    public String getAppName() {
        return params.get("AppName");
    }

    // @return Locale Application Locale
    public Locale getLocale() {
        return locale;
    }

    // @return int Application Width
    public int getDefaultWidth() {
        return 1024;
    }

    // @return int Application Height
    public int getDefaultHeight() {
        return 568;
    }

    // @return int Application Login Width
    public int getLoginWidth() {
        return 365;
    }

    // @return int Application Login Height
    public int getLoginHeight() {
        return 500;
    }

    // @return String Username
    public String getUsername() { return this.username; }

    public void setUsername(String username) {
        this.username = username;
    }
}
