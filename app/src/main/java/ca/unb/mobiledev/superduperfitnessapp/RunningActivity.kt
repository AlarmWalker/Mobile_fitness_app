package ca.unb.mobiledev.superduperfitnessapp

import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.superduperfitnessapp.util.LocationJsonUtils
import java.text.SimpleDateFormat
import java.util.*

class RunningActivity: AppCompatActivity() {
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    private lateinit var Timer : TextView
    private lateinit var Distance : TextView
    private lateinit var timeTest : Calendar
    private lateinit var pauseButton : Button

    // Prototype
    private lateinit var lastLocation : Location
    private lateinit var locationText: TextView
    private lateinit var locationManager : LocationManager
    private var count = 0
    private lateinit var timeArray : IntArray
    private lateinit var player : MediaPlayer
    private val maxVolume : Double = 10.0
    private var currVolume : Double = 5.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_activity)
        Timer = findViewById<TextView>(R.id.timer_text)
        Distance = findViewById<TextView>(R.id.distance_text)

        timeTest = Calendar.getInstance()
        timeTest.set(Calendar.HOUR_OF_DAY, 0)
        timeTest.set(Calendar.MINUTE, 0)
        timeTest.set(Calendar.SECOND, 0)

        pauseButton = findViewById<Button>(R.id.pause_button)
        pauseButton.setText("Start")

        pauseButton.setOnClickListener {
            runClock()
        }

        // Prototype testing functions
        val utils = LocationJsonUtils(applicationContext)
        val locations = utils.getLocations()
        lastLocation = Location("")
        lastLocation.longitude = -66.647
        lastLocation.latitude = 45.940
        locationText = findViewById(R.id.locationText)

        timeArray = intArrayOf(80,60,50,140,10,150,40)
        val intent = intent
        val extras = intent.extras


        val locationButton = findViewById<Button>(R.id.location_button)
        locationButton.setOnClickListener {
            if (count == 6) {
                locationButton.isEnabled = false
            }
            val fileName = extras?.getString("soundTitle")
            audioPlayer(fileName.toString())

            onLocationChanged(locations.get(count), count)
            count++
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
        if (mActive) {
            mActive = false
            pauseButton.setText("Resume")
        }
        else {
            mActive = true
            pauseButton.setText("Pause")
        }

        mHandler.post(mRunnable)
    }

    private fun getTime(): String? {
        timeTest.add(Calendar.SECOND, 1)
        return sdf.format(timeTest.time)
    }

    // Location tracking function
    private fun onLocationChanged (loc : Location, count : Int) : Float {
        val distance = lastLocation.distanceTo(loc)
        val time = timeArray[count]

        locationText.setText("Last location:\n\tLat:\t\t\t\t\t\t" + lastLocation.latitude
                + "\n\tLong:\t\t\t\t" + lastLocation.longitude + "\nCurrent location:\n\tLat:\t\t\t\t\t\t"
                + loc.latitude + "\n\tLong:\t\t\t\t" + loc.longitude + "\n\nDistance:\t\t" + distance
                + "\nTime:\t\t\t\t\t" + time + "\nSpeed:\t\t\t\t" + distance/time)

        lastLocation = loc

        currVolume = (distance/time).toDouble()
        val log1 = (Math.log(maxVolume - (10-currVolume)) / Math.log(maxVolume)).toFloat()
        player.setVolume(log1, log1)

        return distance
    }

    private fun audioPlayer(fileName: String)
    {
        if (!this::player.isInitialized) {
            val afd = assets.openFd(fileName)
            player = MediaPlayer()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()
        }

        player.start()
        /*
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
        }

         */
    }
}
