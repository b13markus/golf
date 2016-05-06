package com.golfapp.test.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefStore {
    private static Activity mAct;
    private static Context context;
    private String PREFS_NAME = "Golf App User 22";

    public PrefStore(Activity aAct) {
        PREFS_NAME = PREFS_NAME + " Language " + getLanguage();
        this.mAct = aAct;
    }

    public PrefStore(Activity aAct, String common) {
        PREFS_NAME = PREFS_NAME + common;
        this.mAct = aAct;
    }

    public int getLanguage() {
        String lang = Locale.getDefault().getLanguage();
        int cc = 2;
        switch (lang) {
            case "en":
                cc = 1;
                break;
            case "fr":
                cc = 2;
                break;
            case "de":
                cc = 3;
                break;
            case "es":
                cc = 4;
                break;
            case "it":
                cc = 5;
                break;
        }
        return cc;
    }


    public PrefStore(Context context) {
        PREFS_NAME = PREFS_NAME + " Language " + getLanguage();
        this.context = context; // TODO Auto-generated constructor stub
    }


    public void log(String string) {
        // BaseActivity.log("PrefStore" + string);
    }

    // ================get and set string

    public String getString(String key) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        String userName = settings.getString(key, null);
        return userName;
    }

    public void setString(String key, String value) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // ======================get set boolean

    public boolean getBoolean(String key) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean userName = settings.getBoolean(key, false);
        return userName;
    }

    // get boolean with custom return value
    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean userName = settings.getBoolean(key, defaultValue);
        return userName;
    }


    public void setBoolean(String key, Boolean value) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {

        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        int value = settings.getInt(key, defaultValue);
        return value;
    }

    public void setInt(String key, int value) {
        SharedPreferences settings = null;
        if (mAct != null)
            settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        else
            settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }


}