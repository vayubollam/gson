package suncor.com.android.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.di.viewmodel.ViewModelKey;
import suncor.com.android.ui.enrollment.form.EnrollmentFormViewModel;
import suncor.com.android.ui.enrollment.form.SecurityQuestionViewModel;
import suncor.com.android.ui.home.dashboard.DashboardViewModel;
import suncor.com.android.ui.home.stationlocator.StationsViewModel;
import suncor.com.android.ui.home.stationlocator.favourites.FavouritesViewModel;
import suncor.com.android.ui.home.stationlocator.search.SearchViewModel;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.login.LoginViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);


    /*
     * This method basically says
     * inject this object into a Map using the @IntoMap annotation,
     * with the  class as key,
     * and a Provider that will build a MovieListViewModel
     * object.
     *
     * */

    //HomeActivity ViewModels
    @Binds
    @IntoMap
    @ViewModelKey(StationsViewModel.class)
    protected abstract ViewModel stationsViewModel(StationsViewModel stationsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel.class)
    protected abstract ViewModel dashboardViewModel(DashboardViewModel dashboardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    protected abstract ViewModel searchViewModel(SearchViewModel searchViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel.class)
    protected abstract ViewModel favouritesViewModel(FavouritesViewModel favouritesViewModel);


    //EnrollmentActivity ViewModels
    @Binds
    @IntoMap
    @ViewModelKey(EnrollmentFormViewModel.class)
    protected abstract ViewModel enrollmentViewModel(EnrollmentFormViewModel enrollmentFormViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SecurityQuestionViewModel.class)
    protected abstract ViewModel securityQuestionViewModel(SecurityQuestionViewModel securityQuestionViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    protected abstract ViewModel loginViewModel(LoginViewModel loginViewModel);
}