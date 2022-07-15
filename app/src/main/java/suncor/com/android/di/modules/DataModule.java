package suncor.com.android.di.modules;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.worklight.wlclient.api.WLAuthorizationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import suncor.com.android.BuildConfig;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.account.EnrollmentsApi;
import suncor.com.android.data.account.EnrollmentsApiImpl;
import suncor.com.android.data.cards.CardsApi;
import suncor.com.android.data.cards.CardsApiImpl;
import suncor.com.android.data.carwash.CarwashApi;
import suncor.com.android.data.carwash.CarwashApiImpl;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.favourite.FavouriteRepositoryImpl;
import suncor.com.android.data.pap.PapApi;
import suncor.com.android.data.pap.PapApiImpl;
import suncor.com.android.data.payments.PaymentsApi;
import suncor.com.android.data.payments.PaymentsApiImpl;
import suncor.com.android.data.profiles.ProfilesApi;
import suncor.com.android.data.profiles.ProfilesApiImpl;
import suncor.com.android.data.redeem.MerchantsApi;
import suncor.com.android.data.redeem.MerchantsApiImpl;
import suncor.com.android.data.redeem.OrderApi;
import suncor.com.android.data.redeem.OrderApiImpl;
import suncor.com.android.data.resetpassword.ForgotPasswordProfileApi;
import suncor.com.android.data.resetpassword.ForgotPasswordProfileApiImpl;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.data.settings.SettingsApiImpl;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.data.stations.StationsApiImpl;
import suncor.com.android.data.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.data.suggestions.GooglePlaceSuggestionsProvider;
import suncor.com.android.data.suggestions.PlaceSuggestionsProvider;
import suncor.com.android.data.transcations.TransactionApi;
import suncor.com.android.data.transcations.TransactionApiImpl;
import suncor.com.android.data.users.UsersApi;
import suncor.com.android.data.users.UsersApiImpl;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.utilities.SharedPrefsHelper;
import suncor.com.android.utilities.UserLocalSettings;

@Module
public class DataModule {
    @Provides
    @Singleton
    StationsApi providesStationsProvider(Gson gson) {
        return new StationsApiImpl(gson);
    }

    @Provides
    @Singleton
    FavouriteRepository providesFavouriteRepository(SessionManager sessionManager, Gson gson) {
        return new FavouriteRepositoryImpl(sessionManager, gson);
    }

    @Provides
    @Singleton
    EnrollmentsApi providesEnrollmentApi(Gson gson) {
        return new EnrollmentsApiImpl(gson);
    }

    @Provides
    @Singleton
    UsersApi providesUsersApi() {
        return new UsersApiImpl();
    }

    @Provides
    @Singleton
    CardsApi providesCardsApi(Gson gson) {
        return new CardsApiImpl(gson);
    }

    @Provides
    @Singleton
    PaymentsApi providesPaymentsApi(Gson gson) {
        return new PaymentsApiImpl(gson);
    }

    @Provides
    @Singleton
    PlacesClient providesPlacesClient(SuncorApplication application) {
        Places.initialize(application, BuildConfig.GOOGLE_API_KEY);
        return Places.createClient(application);
    }

    @Provides
    ProfilesApi providesProfilesApi(Gson gson) {
        return new ProfilesApiImpl(gson);
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
    TransactionApi provideTransactionApi(Gson gson) {
        return new TransactionApiImpl(gson);
    }

    @Provides
    @Singleton
    PapApi providesPapApi(Gson gson) {
        return new PapApiImpl(gson);
    }

    @Provides
    SettingsApi provideSettingsApi(Gson gson, SharedPrefsHelper sharedPrefsHelper) {
        return new SettingsApiImpl(gson, sharedPrefsHelper);
    }

    @Provides
    @Singleton
    MerchantsApi providesMerchantsRepository(Gson gson) {
        return new MerchantsApiImpl(gson);
    }

    @Provides
    @Singleton
    OrderApi providesOrderApi(Gson gson) {
        return new OrderApiImpl(gson);
    }

    @Provides
    @Singleton
    OrderResponse providesOrderResponse() {
        return new OrderResponse();
    }

    @Provides
    @Singleton
    ForgotPasswordProfileApi providesForgotPasswordProfileApi(Gson gson) {
        return new ForgotPasswordProfileApiImpl(gson);
    }

    @Provides
    @Singleton
    CarwashApi providesCarwashApi() {
        return new CarwashApiImpl();
    }
}
