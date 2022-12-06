package ca.unb.mobiledev.superduperfitnessapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ca.unb.mobiledev.superduperfitnessapp.db.db2
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class RunningActivity: AppCompatActivity() {
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("H:mm:ss")

    private lateinit var timerText : TextView
    private lateinit var elapsedTime : Calendar
    private lateinit var startButton : Button
    private lateinit var endButton : Button
    private lateinit var countdownText : TextView
    private lateinit var distanceText: TextView
    private lateinit var messageText: TextView
    private lateinit var distanceBar : SeekBar
    private lateinit var player : MediaPlayer
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var prevLocation : Location

    private var startTime : Long = 0
    private var distanceFromPlayer : Double = 100.0
    private var entitySpeed : Long = 8
    private var totalDistance : Double = 0.0
    private val maxVolume : Double = 10.0

    private var lost = false
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_activity)
        initSharedPreferences()

        entitySpeed = prefs!!.getInt(SPEED_KEY, 0).toLong()

        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        timerText = findViewById(R.id.timer_text)

        elapsedTime = Calendar.getInstance()
        elapsedTime.set(Calendar.HOUR_OF_DAY, 0)
        elapsedTime.set(Calendar.MINUTE, 0)
        elapsedTime.set(Calendar.SECOND, 0)

        distanceText = findViewById(R.id.distanceText)
        messageText = findViewById(R.id.messageText)
        countdownText = findViewById(R.id.countDown_text)

        distanceBar = findViewById(R.id.distanceBar)
        distanceBar.max = 100

        startTime = elapsedTime.timeInMillis

        // Create an instance of the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startButton = findViewById(R.id.startRun_button)
        startButton.setOnClickListener { start() }

        endButton = findViewById(R.id.end_button)
        endButton.text = getString(R.string.end_button)
        endButton.setOnClickListener {
            if (lost) {
                val intent = Intent(this, MainActivity::class.java)
                val extras = intent.extras

                try {
                    ContextCompat.startActivity(this, intent, extras)
                } catch (ex: ActivityNotFoundException) {
                    Log.e("Intent", "Unable to load activity", ex)
                }

            }
            else {
                lost()
            }
        }

        // Setting initial view visibilities
        viewEnable(startButton)
        viewEnable(countdownText)

        viewDisable(timerText)
        viewDisable(distanceText)
        viewDisable(messageText)
        viewDisable(endButton)
        viewDisable(distanceBar)
    }

    private fun viewEnable(view : View) {
        view.isEnabled = true
        view.isClickable = true
        view.isVisible = true
    }

    private fun viewDisable(view: View) {
        view.isEnabled = false
        view.isClickable = false
        view.isVisible = false
    }

    private fun start() {
        viewDisable(startButton)

        countdownText.text = "5"
        Handler(Looper.getMainLooper()).postDelayed({
            val timer = object: CountDownTimer(5000, 250) {

                override fun onTick(millisUntilFinished: Long) {
                    if (millisUntilFinished < 1000) {
                        countdownText.text = "1"
                    }
                    else {
                        countdownText.text =(millisUntilFinished/1000).toString()
                    }
                }

                override fun onFinish() {
                    countdownText.isEnabled = false
                    viewDisable(countdownText)

                    val intent = intent
                    val extras = intent.extras

                    viewEnable(timerText)
                    viewEnable(distanceText)
                    viewEnable(messageText)
                    viewEnable(endButton)
                    viewEnable(distanceBar)

                    distanceBar.isEnabled = false

                    val resId = resources.getIdentifier(
                        extras?.getString("thumbnail"),
                        "drawable",
                        applicationContext.packageName
                    )
                    distanceBar.thumb = resources.getDrawable(resId, theme)

                    val fileName = extras?.getString("soundTitle")
                    audioPlayer(fileName.toString())

                    runClock()
                    lastLocation
                }
            }
            timer.start()
        }, 1000)
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mActive) {
                timerText.setText(getTime())
                mHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun runClock() {
        mActive = !mActive

        mHandler.post(mRunnable)
    }

    private fun getTime(): String? {
        elapsedTime.add(Calendar.SECOND, 1)
        return sdf.format(elapsedTime.time)
    }

    private fun audioPlayer(fileName: String)
    {
        if (!this::player.isInitialized) {
            val afd = assets.openFd(fileName)
            player = MediaPlayer()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.isLooping = true
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
                            if (!this::prevLocation.isInitialized) {
                                prevLocation = lastLocation
                            }

                            requestNewLocationData()
                            locationUpdate(lastLocation)

                            prevLocation = lastLocation
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                distanceText.text = getString(R.string.fetch_location_error)
                                requestNewLocationData()
                            }, 500)
                        }
                    }
            } else {
                Toast.makeText(this, "Please turn location services on", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

    private fun locationUpdate(lastLocation : Location) {
        // Sound adjustment

        var time = UPDATE_INTERVAL/1000
        var speed = prevLocation.distanceTo(lastLocation)/time

        if ((elapsedTime.timeInMillis-startTime)/1000 < UPDATE_INTERVAL/1000) {
            time = 0
            speed = 0F
        }

        distanceFromPlayer -= time*(entitySpeed-speed)
        totalDistance += time*speed

        val formatTotal = String.format("%.2f", totalDistance)
        var formatDistance = String.format("%.2f", distanceFromPlayer)

        if (distanceFromPlayer < 0) {
            formatDistance = "0.00"
        }

        distanceText.text = getString(R.string.location_tracking, formatTotal, formatDistance)

        // Current setting is 100m as the max distance for sound
        var log1 = log(maxVolume-(9*distanceFromPlayer/100), 10.0).toFloat()
        if (distanceFromPlayer > 100) {
            log1 = 0.5f
            distanceBar.progress = 0
        }
        else {
            distanceBar.progress = (100-distanceFromPlayer.toInt())
        }
        player.setVolume(log1, log1)

        // Entity caught up
        if (distanceFromPlayer <= 0) { lost() }

    }

    private fun lost() {
        lost = true
        endButton.text = getString(R.string.return_button)
        player.stop()
        fusedLocationClient!!.removeLocationUpdates(mLocationCallback)

        runClock()

        val formattedDistance = String.format("%.2f", totalDistance)
        messageText.text = getString(R.string.lost_text, formattedDistance, ((elapsedTime.timeInMillis-startTime)/1000).toString())

        val status = db2(applicationContext).addRecord(MainActivity.userName, (elapsedTime.timeInMillis-startTime)/1000, entitySpeed)

        if(status > -1) {
            Toast.makeText(this, "RecordAdded ", Toast.LENGTH_SHORT).show()
        } else{
            Log.i("sql", "unable to add the record")
        }
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
            .setIntervalMillis(UPDATE_INTERVAL)
            .setMaxUpdateDelayMillis(UPDATE_INTERVAL)
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

    private fun initSharedPreferences() {
        prefs = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    override fun onBackPressed() {}

    companion object {
        private const val PREFS_FILE_NAME = "AppPrefs"
        private const val SPEED_KEY = "SPEED_KEY"
        private const val TAG = "TAG"
        private const val UPDATE_INTERVAL: Long = 1000
        private const val LOCATION_REQUEST = 101
    }
}
