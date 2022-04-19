package suncor.com.android.ui.main.stationlocator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
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
import suncor.com.android.model.Resource
import suncor.com.android.model.station.Station
import suncor.com.android.utilities.LocationUtils

@RunWith(MockitoJUnitRunner::class)
class StationViewModelTest {

    @Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var stationViewModel: StationsViewModel

    private val DEFAULT_DISTANCE_API = 25000

    @Mock
    private lateinit var favouriteRepository: FavouriteRepository

    @Mock
    private lateinit var stationsApi: StationsApi

    private val _userLocation = MutableLiveData<LatLng>()

    @Before
    fun setUp() {
        stationViewModel = StationsViewModel(stationsApi, favouriteRepository)
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
    fun test_getLastGpsLocation() {
        val lastLocation = stationViewModel.lastGpsLocation
        Assert.assertNotNull(lastLocation)
    }

    @Test
    fun given_Lat_Long_SetUserLocation() {
        val latLng = LatLng(55.546931281759264, -118.78805667186224)
        stationViewModel.setUserLocation(latLng, StationsViewModel.UserLocationType.GPS)
        Assert.assertEquals(latLng, stationViewModel.userLocation)
    }

}
