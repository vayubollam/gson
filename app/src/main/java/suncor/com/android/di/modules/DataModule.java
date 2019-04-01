package suncor.com.android.di.modules;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import suncor.com.android.BuildConfig;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.account.EnrollmentsApi;
import suncor.com.android.data.repository.account.EnrollmentsApiImpl;
import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.data.repository.favourite.FavouriteRepositoryImpl;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.data.repository.stations.StationsProviderImpl;
import suncor.com.android.data.repository.suggestions.GooglePlaceSuggestionsProvider;
import suncor.com.android.data.repository.suggestions.PlaceSuggestionsProvider;
import suncor.com.android.mfp.SessionManager;

@Module
public class DataModule {
    @Provides
    @Singleton
    StationsProvider providesStationsProvider() {
        return new StationsProviderImpl();
    }

    @Provides
    @Singleton
    FavouriteRepository providesFavouriteRepository(SessionManager sessionManager) {
        return new FavouriteRepositoryImpl(sessionManager);
    }

    @Provides
    @Singleton
    EnrollmentsApi providesEnrollmentApi() {
        return new EnrollmentsApiImpl();
    }

    @Provides
    @Singleton
    PlacesClient providesPlacesClient(SuncorApplication application) {
        Places.initialize(application, BuildConfig.MAP_API_KEY);
        return Places.createClient(application);
    }

    //Not singleton because a fresh token is needed each time the screen is opened
    @Provides
    PlaceSuggestionsProvider providePlaceSuggestionsProvider(PlacesClient client) {
        return new GooglePlaceSuggestionsProvider(client);
    }
}
