package ca.unb.mobiledev.superduperfitnessapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ca.unb.mobiledev.superduperfitnessapp.util.LocationJsonUtils
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*

class RunningActivity: AppCompatActivity() {
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    private lateinit var Timer : TextView
    private lateinit var timeTest : Calendar

    // Location provider
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var prevLocation : Location
    private var startTime : Long = 0

    // Prototype
    private lateinit var locationText: TextView
    private lateinit var locationText2: TextView
    private lateinit var player : MediaPlayer
    private val maxVolume : Double = 10.0
    private var currVolume : Double = 5.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_activity)

        runClock()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Timer
        Timer = findViewById<TextView>(R.id.timer_text)

        timeTest = Calendar.getInstance()
        timeTest.set(Calendar.HOUR_OF_DAY, 0)
        timeTest.set(Calendar.MINUTE, 0)
        timeTest.set(Calendar.SECOND, 0)

        /*
        // Prototype testing functions
        val utils = LocationJsonUtils(applicationContext)
        val locations = utils.getLocations()
        lastLocation = Location("")
        lastLocation.longitude = -66.647
        lastLocation.latitude = 45.940
        locationText = findViewById(R.id.locationText)
        locationText2 = findViewById(R.id.locationText2)
        locationText3 = findViewById(R.id.locationText3)

        timeArray = intArrayOf(80,60,50,140,10,150,40)
         */

        val intent = intent
        val extras = intent.extras
        locationText = findViewById(R.id.locationText)
        locationText2 = findViewById(R.id.locationText2)

        startTime = timeTest.timeInMillis

        // Create an instance of the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationButton = findViewById<Button>(R.id.location_button)
        locationButton.setOnClickListener {
            //val fileName = extras?.getString("soundTitle")
            //audioPlayer(fileName.toString())

            lastLocation
        }
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mActive) {
                Timer.setText(getTime())
                mHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun runClock() {
        mActive = !mActive

        mHandler.post(mRunnable)
    }

    private fun getTime(): String? {
        timeTest.add(Calendar.SECOND, 1)
        return sdf.format(timeTest.time)
    }

    /*
    // Location tracking function
    private fun onLocationChanged (loc : Location, count : Int) : Float {
        val distance = lastLocation.distanceTo(loc)
        val time = timeArray[count]

        /*
        locationText.setText("Last location:\n\tLat:\t\t\t\t\t\t" + lastLocation.latitude
                + "\n\tLong:\t\t\t\t" + lastLocation.longitude + "\nCurrent location:\n\tLat:\t\t\t\t\t\t"
                + loc.latitude + "\n\tLong:\t\t\t\t" + loc.longitude + "\n\nDistance:\t\t" + distance
                + "\nTime:\t\t\t\t\t" + time + "\nSpeed:\t\t\t\t" + distance/time)
         */

        locationText.setText("Last location:\n\nLat:\t\t\t\t" + lastLocation.latitude
                + "\nLong:\t\t" + lastLocation.longitude)
        locationText2.setText("Current location:\n\nLat:\t\t\t\t" + loc.latitude + "\nLong:\t\t"
                + loc.longitude)
        locationText3.setText("Distance:\t\t" + distance + "\nTime:\t\t\t\t\t" + time + "\nSpeed:\t\t\t\t" + distance/time)

        currVolume = (distance/time).toDouble()
        val log1 = (Math.log(maxVolume - (10-currVolume)) / Math.log(maxVolume)).toFloat()
        player.setVolume(log1, log1)

        lastLocation = loc

        return distance
    }
     */


    private fun audioPlayer(fileName: String)
    {
        if (!this::player.isInitialized) {
            val afd = assets.openFd(fileName)
            player = MediaPlayer()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()
        }

        player.start()
    }

    /*
    Location tracking services
     */

    // Got last known location. In some rare situations this can be null.
    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        get() {
            checkPermissions()
            if (isLocationEnabled) {
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener(this) { lastLocation: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (lastLocation != null) {
                            Log.e("lastLocation", "Running")
                            if (!this::prevLocation.isInitialized) {
                                prevLocation = lastLocation
                            }
                            requestNewLocationData()
                            setTextViewDisplay(lastLocation)
                            prevLocation = lastLocation
                        } else {
                            locationText!!.text = getString(R.string.fetch_location_error)
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
        Log.e("setTextViewDisplay", "Running")
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        val accuracy = location.accuracy.toString()

        val time = (timeTest.timeInMillis - startTime)/1000
        val speed = (prevLocation.distanceTo(location)/time).toString()
        val text = getString(R.string.location_details, latitude, longitude, accuracy, time.toString(), speed)
        val text2 = getString(R.string.location_details2, prevLocation.latitude.toString(),
            prevLocation.longitude.toString(), prevLocation.accuracy.toString())
        locationText!!.text = text
        locationText2!!.text = text2
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
        val locationRequest = LocationRequest.Builder(UPDATE_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setIntervalMillis(5000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { lastLocation }
        }
    }

    companion object {
        private const val TAG = "TAG"
        private const val UPDATE_INTERVAL: Long = 5000
        private const val LOCATION_REQUEST = 101
    }
}
