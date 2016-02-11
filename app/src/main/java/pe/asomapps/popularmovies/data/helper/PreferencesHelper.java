package pe.asomapps.popularmovies.data.helper;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Set;

import pe.asomapps.popularmovies.App;

/**
 * Created by Danihelsan
 */
public class PreferencesHelper {
    private final String DEFAULT_PREFERENCES = "default";
    public final String KEY_SHORTCUT_INSTALLED = "shortcut_installed";
    SharedPreferences appPreferences;

    public PreferencesHelper(App app) {
        appPreferences = app.getSharedPreferences(DEFAULT_PREFERENCES, Activity.MODE_PRIVATE);
    }

    public SharedPreferences getAppPreferences(){
        return appPreferences;
    }

    public boolean isShortcutInstalled(){
        return appPreferences.getBoolean(KEY_SHORTCUT_INSTALLED,false);
    }

    public boolean saveValue(String key, Object value){
        SharedPreferences.Editor editor = appPreferences.edit();
        if (value instanceof String){
            appPreferences.edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer){
            appPreferences.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Boolean){
            appPreferences.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float){
            appPreferences.edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Long){
            appPreferences.edit().putLong(key, (Long) value).commit();
        } else if (value instanceof Set){
            appPreferences.edit().putStringSet(key, (Set<String>) value).commit();
        } else{
            return false;
        }
        return true;
    }
}
