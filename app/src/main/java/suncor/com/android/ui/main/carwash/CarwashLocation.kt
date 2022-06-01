package suncor.com.android.ui.main.carwash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import suncor.com.android.LocationLiveData
import suncor.com.android.R
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.model.Resource
import suncor.com.android.ui.main.MainViewModel
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.home.HomeViewModel
import suncor.com.android.ui.main.stationlocator.StationItem
import suncor.com.android.utilities.LocationUtils
import suncor.com.android.utilities.PermissionManager
import suncor.com.android.utilities.PermissionManager.PermissionAskListener
import suncor.com.android.utilities.StationsUtil
import javax.inject.Inject

abstract class CarwashLocation: MainActivityFragment() {

    protected lateinit var carWashCardViewModel: CarWashCardViewModel
    protected lateinit var mainViewModel: MainViewModel

    @Inject lateinit  var viewModelFactory: ViewModelFactory

    private lateinit var locationLiveData: LocationLiveData

    private val REQUEST_CHECK_SETTINGS = 100
    private val PERMISSION_REQUEST_CODE = 1

    private val IS_FIRST_TIME_ACCESS_CAR_WASH = "IS_FIRST_TIME_ACCESS_CAR_WASH"

    @Inject lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the viewModel
        carWashCardViewModel = ViewModelProvider(this, viewModelFactory).get(CarWashCardViewModel::class.java)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initLocation()
        checkAndRequestCarWashPermission()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        carWashCardViewModel.onAttached()
        checkAndRequestPermission()
    }

    private fun initLocation() {
        locationLiveData = LocationLiveData(context?.applicationContext)
        carWashCardViewModel.locationServiceEnabled.observe(viewLifecycleOwner, { enabled: Boolean ->
            if (enabled) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    carWashCardViewModel.getIsLoading().set(carWashCardViewModel.userLocation == null)
                    locationLiveData.observe(viewLifecycleOwner, { location: Location ->
                        carWashCardViewModel.userLocation = LatLng(location.latitude, location.longitude)
                    })
                }
            }
        })

        carWashCardViewModel.refreshLocationCard.observe(viewLifecycleOwner, { checkAndRequestPermission() })

        carWashCardViewModel.nearestStation.observeForever { resource: Resource<StationItem?> ->
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                mainViewModel.setNearestStation(resource.data!!.station)
            }
        }

        carWashCardViewModel.isNearestStationIndependent.observe(viewLifecycleOwner, { isIndependent: Boolean ->
            if (isIndependent) {
                StationsUtil.showIndependentStationAlert(context)
            }
        })
    }

    val tryAgainLister = View.OnClickListener {
        if (carWashCardViewModel.userLocation != null) {
            carWashCardViewModel.isLoading.set(true)
            carWashCardViewModel.userLocation = carWashCardViewModel.userLocation
        } else {
            carWashCardViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(context))
        }
    }

    val openSettingListener = View.OnClickListener {
        permissionManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION,
                object : PermissionAskListener {
                    override fun onNeedPermission() {
                        showRequestLocationDialog(false)
                    }

                    override fun onPermissionPreviouslyDenied() {
                        //in case in the future we would show any rational
                        showRequestLocationDialog(false)
                    }

                    override fun onPermissionPreviouslyDeniedWithNeverAskAgain() {
                        showRequestLocationDialog(true)
                    }

                    override fun onPermissionGranted() {
                        showRequestLocationDialog(false)
                    }
                }
        )
    }

    fun showRequestLocationDialog(previouselyDeniedWithNeverASk: Boolean) {
        val adb = AlertDialog.Builder(requireContext())
        adb.setTitle(R.string.enable_location_dialog_title)
        adb.setMessage(R.string.enable_location_dialog_message)
        adb.setNegativeButton(R.string.cancel, null)
        adb.setPositiveButton(R.string.ok) { dialog, _ ->
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !LocationUtils.isLocationEnabled(context)) {
                LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS)
                return@setPositiveButton
            }
            permissionManager.setFirstTimeAsking(Manifest.permission.ACCESS_FINE_LOCATION, false)
            if (previouselyDeniedWithNeverASk) {
                PermissionManager.openAppSettings(activity)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            }
            dialog.dismiss()
        }
        val alertDialog = adb.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (LocationUtils.isLocationEnabled(context)) {
                    carWashCardViewModel.setLocationServiceEnabled(true)
                } else {
                    LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            carWashCardViewModel.setLocationServiceEnabled(true)
        }
    }

    fun checkAndRequestPermission() {
        permissionManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, object : PermissionAskListener {
            override fun onNeedPermission() {
                if (!permissionManager.isAlertShown) {
                    permissionManager.isAlertShown = true
                    showRequestLocationDialog(false)
                }
            }

            override fun onPermissionPreviouslyDenied() {
                //in case in the future we would show any rational
                showRequestLocationDialog(false)
            }

            override fun onPermissionPreviouslyDeniedWithNeverAskAgain() {
                showRequestLocationDialog(true)
            }
            override fun onPermissionGranted() {
                if (!LocationUtils.isLocationEnabled(context))
                    showRequestLocationDialog(false)

                carWashCardViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(context))
            }
        })
    }

    private fun checkAndRequestCarWashPermission() {
        permissionManager.checkCarWashPermission(context, IS_FIRST_TIME_ACCESS_CAR_WASH) {
            showRequestLocationDialog(false)
        }
    }
}