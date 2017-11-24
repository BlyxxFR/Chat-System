package com.langram.utils;

import java.util.HashMap;
import java.util.Locale;

public class Settings {

    private static HashMap<String, String> params = new HashMap<>();
    private static Locale locale = new Locale("fr", "FR");

    // Init locale and params
    public static void init() {
        Locale.setDefault(locale);
        params.put("AppName", "LanGram");
    }

    // @return String Application Name
    public static String getAppName() { return params.get("AppName"); }

    // @return Locale Application Locale
    public static Locale getLocale() { return locale; }

    // @return int Application Width
    public static int getDefaultWidth() { return 1024; }

    // @return int Application Height
    public static int getDefaultHeight() { return 568; }

    // @return int Application Login Width
    public static int getLoginWidth() { return 365; }

    // @return int Application Login Height
    public static int getLoginHeight() { return 500; }
}
