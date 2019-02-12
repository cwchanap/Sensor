package com.helloworld.sensor.sensor

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import io.reactivex.subjects.BehaviorSubject

class MagneticFieldSensor (activity: Activity) : SensorEventListener {
	private val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	private lateinit var sensor: Sensor
	private val value = BehaviorSubject.createDefault(Field())

	private data class Field(val u_x: Float = 0.0f, val u_y: Float = 0.0f, val u_z: Float = 0.0f) {
		fun toList() : List<Float> {
			return listOf(u_x, u_y, u_z)
		}
	}

	init {
		if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
			val sensorList = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD)
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
		Log.d(this.javaClass.name, "value Change")
		value.onNext(Field(event!!.values[0], event.values[1], event.values[2]))
	}

	fun startListen() {
		sensor.also {
			sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
		}
		value.subscribe ({
			Log.d(this.javaClass.name, "Success: $it")
		}, {
			Log.e(this.javaClass.name, "Error: ${it.message}")
		})
	}

	fun cancelListen() {
		sensorManager.unregisterListener(this)
		value.subscribe().dispose()
	}

}