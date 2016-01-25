package pe.asomapps.popularmovies.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pe.asomapps.popularmovies.BuildConfig;
import retrofit.BaseUrl;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author Danihelsan
 */
@Module
public class ApiModule {
    @Provides @Singleton @Named("log") Interceptor provideLogInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
        if (BuildConfig.DEBUG){
            level = HttpLoggingInterceptor.Level.BASIC;
        }
        interceptor.setLevel(level);
        return interceptor;
    }

    @Provides @Singleton @Named("api_key") Interceptor provideApiKeyInterceptor(){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                HttpUrl httpUrl = chain.request().httpUrl().newBuilder().addQueryParameter("api_key", BuildConfig.MOVIES_API_KEY).build();
                return chain.proceed(chain.request().newBuilder().url(httpUrl).build());
            }
        };
        return interceptor;
    }

    @Provides OkHttpClient provideClient(@Named("log") Interceptor logInterceptor, @Named("api_key") Interceptor apikeyInterceptor){
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(logInterceptor);
        client.interceptors().add(apikeyInterceptor);
        return client;
    }

    @Provides @Singleton @Named("simplied") Gson providesSimplifiedGson(){
        Gson gson = new GsonBuilder().create();
        return gson;
    }

    @Provides @Singleton @Named("default") Gson provideDefaultGson(){
        Gson gson = new Gson();
        return gson;
    }

    @Provides @Singleton BaseUrl provideBaseUrl(){
        BaseUrl baseUrl = new BaseUrl() {
            @Override
            public HttpUrl url() {
                return HttpUrl.parse(BuildConfig.MOVIES_BASE_URL);
            }
        };
        return baseUrl;
    }

    @Provides @Singleton Retrofit provideRetrofit(BaseUrl baseUrl, OkHttpClient okHttpClient, @Named("default") Gson gson){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    @Provides @Singleton
    MoviesApi provideMoviesApi(Retrofit retrofit){
        return retrofit.create(MoviesApi.class);
    }

}
