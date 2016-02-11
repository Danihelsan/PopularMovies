package pe.asomapps.popularmovies.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import pe.asomapps.popularmovies.App;
import pe.asomapps.popularmovies.data.helper.DataBaseHelper;
import pe.asomapps.popularmovies.internal.di.modules.ApiModule;
import pe.asomapps.popularmovies.internal.di.modules.AppModule;
import pe.asomapps.popularmovies.ui.activities.HomeActivity;
import pe.asomapps.popularmovies.ui.fragments.BaseFragment;

/**
 * @author Danihelsan
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    App getApplication();

    void inject(HomeActivity homeActivity);
    void inject(BaseFragment fragment);
    void inject(DataBaseHelper dbHelper);
}
