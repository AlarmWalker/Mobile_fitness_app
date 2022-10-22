package ca.unb.mobiledev.superduperfitnessapp

import android.content.ActivityNotFoundException
import android.content.ContentValues
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

class RunningActivity: AppCompatActivity() {
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
    private lateinit var Timer : TextView
    private lateinit var Distance : TextView
    private var startTime : Long = 0
    private lateinit var timeTest : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_activity)
        Timer = findViewById<TextView>(R.id.timer_text)
        Distance = findViewById<TextView>(R.id.distance_text)

        startTime = System.currentTimeMillis()
        timeTest = Calendar.getInstance()
        timeTest.set(Calendar.HOUR_OF_DAY, 0)
        timeTest.set(Calendar.MINUTE, 0)
        timeTest.set(Calendar.SECOND, 0)

        val pauseButton = findViewById<Button>(R.id.pause_button)

        pauseButton.setOnClickListener {
            runClock()
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

        Log.e("Timer", "Times:\nCurrent Time: " + System.currentTimeMillis().toString() + "\nStart Time: " + startTime.toString() + "\nDifference: " + (System.currentTimeMillis()-startTime).toString())
        return sdf.format(Date(System.currentTimeMillis()-startTime))
    }
}