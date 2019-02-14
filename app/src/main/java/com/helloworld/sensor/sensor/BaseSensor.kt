package com.helloworld.sensor.sensor

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import io.reactivex.subjects.BehaviorSubject

class BaseSensor (activity: Activity, sensorType: Int) : SensorEventListener{
	private val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	private var sensor: Sensor? = null
	private val value = BehaviorSubject.createDefault(0.0f)
	private lateinit var callback: (Float)->Unit

	init {
		if (sensorManager.getDefaultSensor(sensorType) != null) {
			val sensorList = sensorManager.getSensorList(sensorType)
			sensor = sensorList.first()
			Log.d(this.javaClass.name, "sensor created")
		} else {
			Log.e(this.javaClass.name, "no sensor can be used")
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		Log.d(this.javaClass.name, "Accuracy Change")
	}

	override fun onSensorChanged(event: SensorEvent?) {
		value.onNext(event!!.values[0])
	}

	fun startListen() {
		sensor.also {
			sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
		}
		value.subscribe ({
			Log.d(this.javaClass.name, "Success: $it")
			callback(it)
		}, {
			Log.e(this.javaClass.name, "Error: ${it.message}")
		})
	}

	fun setListenCallBack(callback: (Float)->Unit) {
		this.callback = callback
	}

	fun cancelListen() {
		sensorManager.unregisterListener(this)
		value.subscribe().dispose()
	}

}