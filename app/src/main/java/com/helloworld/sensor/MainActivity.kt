package com.helloworld.sensor

import android.hardware.Sensor
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.helloworld.sensor.location.AltitudeListener
import com.helloworld.sensor.sensor.BaseSensor
import com.helloworld.sensor.sensor.MagneticFieldSensor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	private lateinit var pressureSensor: BaseSensor
	private lateinit var temperatureSensor: BaseSensor
	private lateinit var humiditySensor: BaseSensor
	private lateinit var magneticFieldSensor: MagneticFieldSensor
	private lateinit var altitudeListener: AltitudeListener

	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		when (item.itemId) {
			R.id.navigation_home -> {
				message.setText(R.string.title_home)
				return@OnNavigationItemSelectedListener true
			}
			R.id.navigation_dashboard -> {
				message.setText(R.string.title_dashboard)
				return@OnNavigationItemSelectedListener true
			}
			R.id.navigation_notifications -> {
				message.setText(R.string.title_notifications)
				return@OnNavigationItemSelectedListener true
			}
		}
		false
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		altitude.text = getString(R.string.altitude, 0.0f)
		address.text = getString(R.string.address, "", "")

		pressureSensor = BaseSensor(this, Sensor.TYPE_PRESSURE)
		temperatureSensor = BaseSensor(this, Sensor.TYPE_AMBIENT_TEMPERATURE)
		humiditySensor = BaseSensor(this, Sensor.TYPE_AMBIENT_TEMPERATURE)
		magneticFieldSensor = MagneticFieldSensor(this)
		altitudeListener = AltitudeListener(this)

		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

		pressureSensor.setListenCallBack {pressure.text = getString(R.string.pressure, it) }
		temperatureSensor.setListenCallBack { temperature.text = getString(R.string.temperature, it) }
		humiditySensor.setListenCallBack { humidity.text = getString(R.string.humidity, it) }
		magneticFieldSensor.setListenCallBack { magneticField.text = getString(R.string.magneticField, it.u_x, it.u_y, it.u_z) }
		altitudeListener.setListenCallBack {
			if (it != null) {
				altitude.text = getString(R.string.altitude, it.altitude)
				val t = altitudeListener.locationToAddress(it.latitude, it.longitude)
				if (t != null) address.text = getString(R.string.address, t[0].featureName, t[0].countryName)
			}
		}

		altitudeListener.checkPermission()

		pressureSensor.startListen()
		temperatureSensor.startListen()
		humiditySensor.startListen()
		magneticFieldSensor.startListen()
		altitudeListener.startUpdateAltitude()
	}

	override fun onPause() {
		super.onPause()
		pressureSensor.cancelListen()
		temperatureSensor.cancelListen()
		humiditySensor.cancelListen()
		magneticFieldSensor.cancelListen()
		altitudeListener.stopUpdateAltitude()
	}
}
