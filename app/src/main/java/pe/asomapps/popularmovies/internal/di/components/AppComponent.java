package pe.asomapps.popularmovies.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;
import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.data.api.ApiModule;
import pe.asomapps.popularmovies.internal.di.modules.AppModule;
import pe.asomapps.popularmovies.ui.fragments.BaseFragment;

/**
 * @author Danihelsan
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    App getApplication();

    void inject(BaseFragment fragment);
}