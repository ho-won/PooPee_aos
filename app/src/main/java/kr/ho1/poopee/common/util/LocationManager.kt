package kr.ho1.poopee.common.util

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kr.ho1.poopee.common.ObserverManager

object LocationManager {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun setLocationListener() {
        locationManager = ObserverManager.context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        try {
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun removeLocationUpdate() {
        locationManager!!.removeUpdates(locationListener)
    }

}