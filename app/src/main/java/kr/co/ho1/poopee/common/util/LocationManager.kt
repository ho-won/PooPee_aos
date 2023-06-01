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
 * 위도(latitude) 1도 : 약 110km
 * 경도(longitude) 1도 : 약 88.74km
 */
object LocationManager {
    const val DISTANCE = 0.02 // +-

    lateinit var locationManager: LocationManager
    lateinit var gpsLocationListener: LocationListener
    lateinit var networkLocationListener: LocationListener
    var locationByGps: Location? = null
    var locationByNetwork: Location? = null

    @SuppressLint("MissingPermission")
    fun setLocationListener() {
        locationManager = ObserverManager.context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        gpsLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
                setCurrentLocation()
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        networkLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork = location
                setCurrentLocation()
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun setCurrentLocation() {
        val lastKnownLocationByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }

        val lastKnownLocationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }

        if (locationByGps != null && locationByNetwork != null) {
            if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                onLocationChanged(locationByGps!!)
            } else {
                onLocationChanged(locationByNetwork!!)
            }
        } else if (locationByGps != null) {
            onLocationChanged(locationByGps!!)
        } else if (locationByNetwork != null) {
            onLocationChanged(locationByNetwork!!)
        }
    }

    fun onLocationChanged(location: Location) {
        SharedManager.setLatitude(location.latitude)
        SharedManager.setLongitude(location.longitude)
        ObserverManager.root!!.onLocationChanged(location)
    }

    fun removeLocationUpdate() {
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
    }

}