package com.helloworld.sensor.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.LocationServices
import java.util.*

open class Locator(private val activity: Activity) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private val geocoder = Geocoder(activity, Locale.getDefault())
    private var permission = 0

    data class Location(val altitude: Double, val latitude: Double, val longitude: Double, val bearing: Float, val speed: Float)
    data class Address(val featureName: String, val latitude: Double, val longitude: Double, val displayCountry: String, val phone: String, val url: String, val countryName: String)

    fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permission)
            }

            return false
        } else return true
    }

    fun getLastKnownLocation(callback: (Location?) -> Unit) {
        try {
            if (checkPermission()) {
                fusedLocationClient.lastLocation.addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.d(this.javaClass.name, "Get Last Know Location Failed, ${it.exception}")
                    } else{
                        val result = it.result
                        Log.d(this.javaClass.name, "Altitude: ${result?.altitude}")

                        if (result != null) callback(Location(result.altitude , result.latitude, result.longitude, result.bearing, result.speed))
                        else callback(null)
                    }
                }
            }

            Log.d(this.javaClass.name, "Finished")
        } catch (ex: SecurityException) {
            Log.e(this.javaClass.name, "No permission", null)
        }
    }

    open fun locationToAddress(latitude: Double?, longitude: Double?) : List<Address>? {
        try {
            latitude ?: throw IllegalArgumentException()
            longitude ?: throw IllegalArgumentException()

            val addresses = geocoder.getFromLocation(latitude, longitude, 10)
            return addresses.map {

                Address(it.featureName, it.latitude, it.longitude, it.locale.displayCountry ?: "",
                        it.phone ?: "", it.url ?: "", it.countryName ?: "")

            }

        } catch (ie: IllegalArgumentException) {
            Log.e(this.javaClass.name, "invalid latitude or longitude")
        } catch (e: Exception) {
            Log.e(this.javaClass.name, "Exception: ${e.message}")
        }
        return null
    }

}