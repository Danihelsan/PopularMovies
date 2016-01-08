package pe.asomapps.popularmovies.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;
import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.internal.di.modules.AppModule;

/**
 * @author Danihelsan
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    App getApplication();
}
