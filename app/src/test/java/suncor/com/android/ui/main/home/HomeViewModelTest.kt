package suncor.com.android.ui.main.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import suncor.com.android.data.DistanceApi;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.mfp.SessionManager;

public class HomeViewModelTest {

    private final SettingsApi settingsApi = Mockito.mock(SettingsApi.class);
    private final PapRepository papRepository = Mockito.mock(PapRepository.class);
    private final FavouriteRepository favouriteRepository = Mockito.mock(FavouriteRepository.class);
    private final SessionManager sessionManager = Mockito.mock(SessionManager.class);
    private final StationsApi stationsApi = Mockito.mock(StationsApi.class);
    private final DistanceApi distanceApi = Mockito.mock(DistanceApi.class);

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private HomeViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new HomeViewModel(sessionManager, stationsApi,
                favouriteRepository, distanceApi,
                papRepository, settingsApi);
    }

    @Test
    public void ifStartTimeIsGreaterThanEndTime_assertTrue() {
        long diff = viewModel.getDateTimeDifference("2022/04/09 14:46:00", "2022/04/08 14:46:00");
        Assert.assertTrue(diff < 0);
    }

    @Test
    public void ifStartTimeIsLesserThanEndTime_assertFalse() {
        long diff = viewModel.getDateTimeDifference("2022/04/09 14:46:00", "2022/04/08 14:46:00");
        Assert.assertFalse(diff > 0);
    }
}
