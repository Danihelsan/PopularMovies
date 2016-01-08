package pe.asomapps.popularmovies;

import android.app.Application;

import pe.asomapps.popularmovies.internal.di.components.AppComponent;
import pe.asomapps.popularmovies.internal.di.components.DaggerAppComponent;
import pe.asomapps.popularmovies.internal.di.modules.AppModule;


/**
 * @author Danihelsan
 */
public class App extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }



}
