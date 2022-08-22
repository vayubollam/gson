package suncor.com.android.ui.main.stationlocator

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
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.cards.CardsRepository
import suncor.com.android.data.favourite.FavouriteRepository
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.data.stations.StationsApi
import suncor.com.android.model.Resource
import suncor.com.android.model.account.CardStatus
import suncor.com.android.model.station.Station
import suncor.com.android.ui.main.stationlocator.StationsViewModel.UserLocationType
import suncor.com.android.utilities.LocationUtils

@RunWith(MockitoJUnitRunner::class)
class StationViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val DEFAULT_DISTANCE_API = 25000

    private val stationsApi = mock(StationsApi::class.java)

    private val favouriteRepository = mock(
        FavouriteRepository::class.java
    )
    private lateinit var mockList: Array<Station>

    @Before
    fun setUp() {
    }

    @Test
    fun given_LocationBound_RefreshStations() {
        val mapCenter: LatLng? = LatLng(55.546931281759264, -118.78805667186224)

        val _25KmBounds = LocationUtils.calculateBounds(
            mapCenter,
            DEFAULT_DISTANCE_API,
            1f
        )
        val locationList= java.util.ArrayList<Station>()

        mockList = Gson().fromJson(locationResponse, Array<Station>::class.java)
        val apiResponse = MutableLiveData<Resource<ArrayList<Station>>>()

        for (location in mockList){
            locationList.add(location)
        }
        apiResponse.postValue(Resource.success(locationList))
        lenient().`when`<LiveData<Resource<ArrayList<Station>>>>(
            stationsApi.getStations(
                _25KmBounds, true
            )
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<ArrayList<Station>> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertNotNull(apiResponse.value?.data)
    }

    @Test
    fun test_getLastGpsLocation() {
        Assert.assertNotNull(getGpsLocation())
    }

    @Test
    fun given_Lat_Long_SetUserLocation() {
        val latLng = LatLng(55.546931281759264, -118.78805667186224)
        val _userLocation = MutableLiveData<LatLng>()
        _userLocation.postValue(latLng)
        val userLocation: LiveData<LatLng> = _userLocation
        userLocation.observeForever {
            if (it.latitude != null && it.longitude != null){
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun testLoadfavourites() {
        val apiResponse = MutableLiveData<Resource<Boolean>>()
        apiResponse.postValue(Resource.success(true))
        lenient().`when`<LiveData<Resource<Boolean>>>(
            favouriteRepository.loadFavourites()
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Boolean> -> }
        apiResponse.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.SUCCESS, apiResponse.value?.status)
    }

    fun getGpsLocation():LatLng{
       return LatLng(55.546931281759264, -118.78805667186224)
   }


    private  val locationResponse=
        " [\n" +
                "   {\n" +
                "     \"amenities\": [\n" +
                "       \"lottery\",\n" +
                "       \"ultra94\",\n" +
                "       \"bankMachine\",\n" +
                "       \"open24Hours\",\n" +
                "       \"convenienceStore\",\n" +
                "       \"carWashBrushTypeSoftcloth\"\n" +
                "     ],\n" +
                "     \"hours\": [\n" +
                "       {\n" +
                "         \"close\": \"2400\",\n" +
                "         \"open\": \"0000\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"close\": \"2400\",\n" +
                "         \"open\": \"0000\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"close\": \"2400\",\n" +
                "         \"open\": \"0000\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"close\": \"2400\",\n" +
                "         \"open\": \"0000\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"distance\": 0.010222641913614331,\n" +
                "      \"phone\": \"(000) 000-0000\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"L3R1M8\",\n" +
                "      \"latitude\": 43.863183221307,\n" +
                "      \"addressLine\": \"4780 Hwy. #7 East\",\n" +
                "      \"crossStreet\": \"NEW KENNEDY\",\n" +
                "      \"primaryCity\": \"Unionville\",\n" +
                "      \"longitude\": -79.3032763914017\n" +
                "    },\n" +
                "    \"id\": \"f3q44R/Jfog/u6o6ck5TUA==\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"amenities\": [\n" +
                "      \"carWashBrandInd\",\n" +
                "      \"lottery\",\n" +
                "      \"restaurant\",\n" +
                "      \"ultra94\",\n" +
                "      \"diesel\",\n" +
                "      \"aw\",\n" +
                "      \"propaneBottleExchange\",\n" +
                "      \"bankMachine\",\n" +
                "      \"open24Hours\",\n" +
                "      \"convenienceStore\",\n" +
                "      \"carWashBrushTypeSoftcloth\"\n" +
                "    ],\n" +
                "    \"hours\": [\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"distance\": 0.010222641913614331,\n" +
                "      \"phone\": \"(000) 000-0000\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"V8C1V6\",\n" +
                "      \"latitude\": 43.8631838521632,\n" +
                "      \"addressLine\": \"4641 Highway 7\",\n" +
                "      \"crossStreet\": \"KENNEDY ROAD\",\n" +
                "      \"primaryCity\": \"Markham\",\n" +
                "      \"longitude\": -79.3032763525844\n" +
                "    },\n" +
                "    \"id\": \"A00ZoBc9a2bgxA7GIxtAYQ==\"\n" +
                "  }\n" +
                "]"
}
