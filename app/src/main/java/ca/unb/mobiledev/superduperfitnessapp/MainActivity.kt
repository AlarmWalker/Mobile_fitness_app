package ca.unb.mobiledev.superduperfitnessapp

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.superduperfitnessapp.db.db
import ca.unb.mobiledev.superduperfitnessapp.util.dbUtil
import java.text.SimpleDateFormat
import java.util.*


class
MainActivity : AppCompatActivity() {

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var mClock : TextView
    private var mActive : Boolean = false
    private val sdf: SimpleDateFormat = SimpleDateFormat("hh:mm:ss")
    private lateinit var database: db

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

        val settingIcon = findViewById<ImageView>(R.id.setting_icon)

        settingIcon.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingActivity::class.java)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Unable to load setting activity", ex)
            }
        }

        database = db(this)
        val loadButton = findViewById<Button>(R.id.loadButton)

        loadButton.setOnClickListener{
            val profileIcon = findViewById<ImageView>(R.id.profileIcon)
            val savedName = findViewById<EditText>(R.id.enterName)
            if(database.getBitmapByName(savedName.text.toString()) != null){
                val bitmap: Bitmap = dbUtil.getImage(database.getBitmapByName(savedName.text.toString())!!)
                profileIcon.setImageBitmap(bitmap)
                userName = savedName.text.toString()
                Toast.makeText(this@MainActivity, "loaded", Toast.LENGTH_SHORT).show()
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

    companion object {
        var userName: String = ""
    }
}