package suncor.com.android.ui.main.home

import org.mockito.Mockito
import suncor.com.android.data.pap.PapRepository
import suncor.com.android.data.favourite.FavouriteRepository
import suncor.com.android.data.stations.StationsApi
import suncor.com.android.data.DistanceApi
import org.junit.rules.TestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import suncor.com.android.ui.main.home.HomeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.mfp.SessionManager

class HomeViewModelTest {
    private val settingsApi = Mockito.mock(
        SettingsApi::class.java
    )
    private val papRepository = Mockito.mock(PapRepository::class.java)
    private val favouriteRepository = Mockito.mock(
        FavouriteRepository::class.java
    )
    private val sessionManager = Mockito.mock(
        SessionManager::class.java
    )
    private val stationsApi = Mockito.mock(StationsApi::class.java)
    private val distanceApi = Mockito.mock(DistanceApi::class.java)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private var viewModel: HomeViewModel? = null
    @Before
    fun setUp() {
        viewModel = HomeViewModel(
            sessionManager, stationsApi,
            favouriteRepository, distanceApi,
            papRepository, settingsApi
        )
    }

    @Test
    fun ifStartTimeIsGreaterThanEndTime_assertTrue() {
        val diff = viewModel!!.getDateTimeDifference("2022/04/09 14:46:00", "2022/04/08 14:46:00")
        Assert.assertTrue(diff < 0)
    }

    @Test
    fun ifStartTimeIsLesserThanEndTime_assertFalse() {
        val diff = viewModel!!.getDateTimeDifference("2022/04/09 14:46:00", "2022/04/08 14:46:00")
        Assert.assertFalse(diff > 0)
    }
}