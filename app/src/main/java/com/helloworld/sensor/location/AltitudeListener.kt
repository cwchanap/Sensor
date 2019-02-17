package com.helloworld.sensor.location

import android.app.Activity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class AltitudeListener(activity: Activity) : Locator(activity) {
    private val interval = 5L
    private lateinit var observable: Disposable

    private lateinit var callback: (Locator.Location?) -> Unit

    fun startUpdateAltitude() {
        observable = Observable.interval(interval, TimeUnit.SECONDS).timeInterval().subscribe {
            Log.d(this.javaClass.name, "Update Altitude")

            getLastKnownLocation(callback)
        }
    }

    fun stopUpdateAltitude() {
        observable.dispose()
    }

    fun setListenCallBack(callback: (Locator.Location?)->Unit) {
        this.callback = callback
    }

}