package suncor.com.android.di.modules;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import suncor.com.android.BuildConfig;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.account.EnrollmentsApi;
import suncor.com.android.data.repository.account.EnrollmentsApiImpl;
import suncor.com.android.data.repository.cards.CardsApi;
import suncor.com.android.data.repository.cards.CardsApiImpl;
import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.data.repository.favourite.FavouriteRepositoryImpl;
import suncor.com.android.data.repository.profiles.ProfilesApi;
import suncor.com.android.data.repository.profiles.ProfilesApiImpl;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.data.repository.stations.StationsProviderImpl;
import suncor.com.android.data.repository.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.data.repository.suggestions.GooglePlaceSuggestionsProvider;
import suncor.com.android.data.repository.suggestions.PlaceSuggestionsProvider;
import suncor.com.android.data.repository.transcations.TransactionApi;
import suncor.com.android.data.repository.transcations.TransactionApiImpl;
import suncor.com.android.data.repository.users.UsersApi;
import suncor.com.android.data.repository.users.UsersApiImpl;
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
    UsersApi providesUsersApi() {
        return new UsersApiImpl();
    }

    @Provides
    @Singleton
    CardsApi providesCardsRepository(Gson gson) {
        return new CardsApiImpl(gson);
    }

    @Provides
    @Singleton
    PlacesClient providesPlacesClient(SuncorApplication application) {
        Places.initialize(application, BuildConfig.MAP_API_KEY);
        return Places.createClient(application);
    }

    @Provides
    ProfilesApi providesProfilesApi() {
        return new ProfilesApiImpl();
    }

    //Not singleton because a fresh token is needed each time the screen is opened
    @Provides
    PlaceSuggestionsProvider providePlaceSuggestionsProvider(PlacesClient client) {
        return new GooglePlaceSuggestionsProvider(client);
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    OkHttpClient providesHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    CanadaPostAutocompleteProvider providesCanadaPostAutocompleteProvider(Gson gson, OkHttpClient okHttpClient) {
        return new CanadaPostAutocompleteProvider(gson, okHttpClient);
    }

    @Provides
    @Singleton
    TransactionApi provideTransactionApi() {
        return new TransactionApiImpl();
    }
}
