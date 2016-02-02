package pe.asomapps.popularmovies.internal.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pe.asomapps.popularmovies.App;

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
}