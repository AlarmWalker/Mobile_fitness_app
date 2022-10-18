package ca.unb.mobiledev.superduperfitnessapp

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var mClock : TextView
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("hh:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mClock = findViewById<TextView>(R.id.time)
        startClock()

        val startButton = findViewById<Button>(R.id.start_button)

        startButton.setOnClickListener {
            val intent = Intent(this@MainActivity, StartActivity::class.java)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to load activity", ex)
            }
        }
    }


    // Main app - Clock
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mActive) {
                mClock.setText(getTime())
                mHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun startClock() {
        mActive = true
        mHandler.post(mRunnable)
    }

    private fun getTime(): String? {
        return sdf.format(Date(System.currentTimeMillis()))
    }


}