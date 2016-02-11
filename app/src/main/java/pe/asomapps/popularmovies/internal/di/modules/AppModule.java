package pe.asomapps.popularmovies.internal.di.modules;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.data.helper.DataBaseHelper;
import pe.asomapps.popularmovies.data.helper.PreferencesHelper;

/**
 * @author Danihelsan
 */
@Module
public class AppModule {
    App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    App providesApplication() {
        return app;
    }

    @Provides
    @Singleton
    DataBaseHelper providesDbHelper(App app){
        return new DataBaseHelper(app);
    }

    @Provides
    @Singleton
    ContentResolver providesContentResolver(App app){
        return app.getContentResolver();
    }

    @Provides
    @Singleton
    PreferencesHelper providesPreferencesHelper(App app){
        return new PreferencesHelper(app);
    }
}