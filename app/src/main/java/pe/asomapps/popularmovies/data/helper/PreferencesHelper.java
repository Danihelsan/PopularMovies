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

    public boolean isShortcutInstalled(){
        return appPreferences.getBoolean(KEY_SHORTCUT_INSTALLED,false);
    }

    public boolean saveValue(String key, Object value){
        SharedPreferences.Editor editor = appPreferences.edit();
        if (value instanceof String){
            editor.putString(key, (String) value).apply();
        } else if (value instanceof Integer){
            editor.putInt(key, (Integer) value).apply();
        } else if (value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Float){
            editor.putFloat(key, (Float) value).apply();
        } else if (value instanceof Long){
            editor.putLong(key, (Long) value).apply();
        } else if (value instanceof Set){
            editor.putStringSet(key, (Set<String>) value).apply();
        } else{
            return false;
        }
        return true;
    }
}
