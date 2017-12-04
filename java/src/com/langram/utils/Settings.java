package com.langram.utils;

import java.util.HashMap;
import java.util.Locale;

public class Settings {

    private HashMap<String, String> params = new HashMap<>();
    private Locale locale = new Locale("fr", "FR");
    private static final Settings instance = new Settings();

    // Get instance
    public static Settings getInstance() {
        return instance;
    }

    // Init locale and params
    private Settings() {
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

}
