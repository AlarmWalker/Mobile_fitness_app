package ca.unb.mobiledev.superduperfitnessapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SoundActivity : AppCompatActivity()
{
    private val maxVolume : Double = 10.0
    private var currVolume : Double = 5.0
    private lateinit var player : MediaPlayer
    private lateinit var upButton : Button
    private lateinit var downButton : Button
    private lateinit var soundButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent
        val extras = intent.extras

        val view = findViewById<TextView>(R.id.description_textview)
        view.text = extras?.getString("description")
        view.movementMethod = ScrollingMovementMethod()

        val actionBar = supportActionBar
        actionBar?.title = extras?.getString("title")


        // Sound button - plays sound associated with id
        soundButton = findViewById<Button>(R.id.sound_button)
        soundButton.setOnClickListener {
            val fileName = extras?.getString("soundTitle")
            audioPlayer(fileName.toString())
        }

        var volumeText = findViewById<TextView>(R.id.test_volume)

        upButton = findViewById(R.id.volUp_button)
        upButton?.visibility = Button.INVISIBLE
        upButton.setOnClickListener {
            currVolume -= 1
            val log1 = (Math.log(maxVolume - currVolume) / Math.log(maxVolume)).toFloat()
            volumeText.setText("Log: " + log1.toString() + "\nCurrent Volume: " + currVolume.toString())
            player.setVolume(log1, log1)
        }

        downButton = findViewById<Button>(R.id.volDown_button)
        downButton?.visibility = Button.INVISIBLE
        downButton.setOnClickListener {
            currVolume += 1
            val log1 = (Math.log(maxVolume - currVolume) / Math.log(maxVolume)).toFloat()
            volumeText.setText("Log: " + log1.toString() + "\nCurrent Volume: " + currVolume.toString())
            player.setVolume(log1, log1)
        }


        // Confirm button - goes to RunningActivity
        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val intent2 = Intent(this, RunningActivity::class.java)

            intent2.putExtra("title",  extras?.getString("title"))
            intent2.putExtra("description", extras?.getString("description"))
            intent2.putExtra("soundTitle", extras?.getString("soundTitle"))

            val extras2 = intent2.extras

            try {
                startActivity(intent2, extras2)
            } catch (ex: ActivityNotFoundException) {
                Log.e("Intent", "Unable to load activity", ex)
            }
        }
    }

    private fun audioPlayer(fileName: String)
    {
        if (!this::player.isInitialized) {
            val afd = assets.openFd(fileName)
            player = MediaPlayer()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()

            upButton?.visibility = Button.VISIBLE
            downButton?.visibility = Button.VISIBLE
        }

        if (player.isPlaying) {
            player.pause()
            soundButton.setText("Resume")
        } else {
            player.start()
            soundButton.setText("Pause")
        }
    }
}