package ca.unb.mobiledev.superduperfitnessapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationUtil : AppCompatActivity() {
    private var mTextView: TextView? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_activity)
        mTextView = findViewById(R.id.locationText)
        val mButton = findViewById<Button>(R.id.location_button)
        mButton.setOnClickListener { lastLocation }

        // Create an instance of the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // Got last known location. In some rare situations this can be null.
    @get:SuppressLint("MissingPermission")
    val lastLocation: Unit
        get() {
            checkPermissions()
            if (isLocationEnabled) {
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener(this) { lastLocation: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (lastLocation != null) {
                            setTextViewDisplay(lastLocation)
                        } else {
                            mTextView!!.text = getString(R.string.fetch_location_error)
                            requestNewLocationData()
                        }
                    }
            } else {
                Toast.makeText(this, "Please turn location services on", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

    private fun setTextViewDisplay(location: Location) {
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        val accuracy = location.accuracy.toString()
        val time = location.time.toString()
        val speed = location.speed.toString()
        val text = getString(R.string.location_details, latitude, longitude, accuracy, time, speed)
        mTextView!!.text = text
    }

    /**
     * Method to determine if the user has granted the appropriate access levels
     */
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestRuntimePermissions()
        }
    }

    /**
     * Grants the appropriate permissions
     */
    private fun requestRuntimePermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        ), LOCATION_REQUEST)
    }

    /**
     * Checks to see if the user has turned on location from Settings
     * @return The location manager object
     */
    private val isLocationEnabled: Boolean
        get() {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST) { // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "onRequestPermissionsResult: Granted")
                lastLocation
            } else {
                Toast.makeText(this, "onRequestPermissionsResult: Denied", Toast.LENGTH_SHORT)
                    .show()
                Log.i(TAG, "onRequestPermissionsResult: Denied")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(UPDATE_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setIntervalMillis(100)
            .setMaxUpdateDelayMillis(100)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { setTextViewDisplay(it) }
        }
    }

    companion object {
        private const val TAG = "TAG"
        private const val UPDATE_INTERVAL: Long = 60 * 60 * 1000
        private const val LOCATION_REQUEST = 101
    }
}