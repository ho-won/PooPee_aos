@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package kr.co.ho1.poopee.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.data.SharedManager

/**
 * 위도 1도 : 약 110km
 * 경도 1도 : 약 88.74km
 */
object LocationManager {
    const val DISTANCE = 0.02 // +-
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    fun setLocationListener() {
        locationManager = ObserverManager.context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                SharedManager.setLatitude(location.latitude)
                SharedManager.setLongitude(location.longitude)
                ObserverManager.root!!.onLocationChanged(location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        try {
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000L, 5f, locationListener!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeLocationUpdate() {
        locationManager!!.removeUpdates(locationListener!!)
    }

}