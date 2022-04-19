package suncor.com.android.ui.main.stationlocator.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.favourite.FavouriteRepository
import suncor.com.android.data.stations.StationsApi
import suncor.com.android.data.suggestions.PlaceSuggestionsProvider
import suncor.com.android.model.Resource
import suncor.com.android.model.station.Station
import suncor.com.android.ui.main.stationlocator.StationItem
import suncor.com.android.utilities.LocationUtils
import suncor.com.android.utilities.UserLocalSettings

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {
    @Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var searchViewModel: SearchViewModel

    private val DEFAULT_DISTANCE_API = 25000

    @Mock
    private lateinit var suggestionsProvider: PlaceSuggestionsProvider

    @Mock
    private lateinit var favouriteRepository: FavouriteRepository

    @Mock
    private lateinit var stationsApi: StationsApi

    private val _userLocation = MutableLiveData<LatLng>()

    @Mock
    private lateinit var userLocalSettings: UserLocalSettings

    protected lateinit var gson: Gson

    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(
            stationsApi,
            suggestionsProvider,
            gson,
            userLocalSettings,
            favouriteRepository
        )
    }


    @Test
    fun given_LocationBound_RefreshStations() {
        _userLocation.observeForever {
        }

        val mapCenter: LatLng? = LatLng(55.546931281759264, -118.78805667186224)

        val _stationsAround = MutableLiveData<Resource<java.util.ArrayList<StationItem>>>()
        // dummy
        _stationsAround.observeForever {

        }
        val _25KmBounds = LocationUtils.calculateBounds(
            mapCenter,
            DEFAULT_DISTANCE_API,
            1f
        )

        val apiResponse = MutableLiveData<Resource<ArrayList<Station>>>()

        _stationsAround.postValue(Resource.success(ArrayList()))
        Mockito.`when`<LiveData<Resource<ArrayList<Station>>>>(
            stationsApi.getStations(
                _25KmBounds, true
            )

        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<ArrayList<Station>> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(Resource.Status.SUCCESS, apiResponse.value?.status)

    }

    @Test
    fun testRescentSearch() {
        val recentSearch = searchViewModel.recentSearches

        Assert.assertNotNull(recentSearch)


    }

    @Test
    fun testIsRecentSearchEmpty() {
        val isrecentEmpty = searchViewModel.isRecentSearchEmpty
        Assert.assertNotNull(isrecentEmpty)
        Assert.assertTrue(!isrecentEmpty)
        Assert.assertFalse(isrecentEmpty)
    }


}