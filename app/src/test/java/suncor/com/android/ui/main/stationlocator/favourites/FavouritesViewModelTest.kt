package suncor.com.android.ui.main.stationlocator.favourites

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
import suncor.com.android.model.Resource

@RunWith(MockitoJUnitRunner::class)
class FavouritesViewModelTest {

    @Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var favouritesViewModel: FavouritesViewModel

    @Mock
    private lateinit var favouriteRepository: FavouriteRepository


    @Before
    fun setUp() {
        favouritesViewModel = FavouritesViewModel(favouriteRepository)
    }

    @Test
    fun test_getRerfreshStation() {
        val stations = favouritesViewModel.refreshStations()
        Assert.assertNotNull(stations)
    }

    @Test
    fun given_Lat_Long_SetUserLocation() {
        val latLng = LatLng(55.546931281759264, -118.78805667186224)
        favouritesViewModel.setUserLocation(latLng)
        Assert.assertEquals(latLng, favouritesViewModel.userLocation)
    }

    @Test
    fun testLoadfavourites() {
        val apiResponse = MutableLiveData<Resource<Boolean>>()
        apiResponse.postValue(Resource.success(true))
        Mockito.`when`<LiveData<Resource<Boolean>>>(
            favouriteRepository.loadFavourites()
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Boolean> -> }
        apiResponse.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.SUCCESS, apiResponse.value?.status)
    }


}