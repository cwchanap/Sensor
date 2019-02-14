package com.helloworld.sensor

import android.hardware.Sensor
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.helloworld.sensor.sensor.BaseSensor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	private lateinit var pressureSensor: BaseSensor

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

		pressureSensor = BaseSensor(this, Sensor.TYPE_PRESSURE)

		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

		pressureSensor.setListenCallBack {
			pressure.text = getString(R.string.pressure, it)
		}
		pressureSensor.startListen()
	}

	override fun onPause() {
		super.onPause()
		pressureSensor.cancelListen()
	}
}
