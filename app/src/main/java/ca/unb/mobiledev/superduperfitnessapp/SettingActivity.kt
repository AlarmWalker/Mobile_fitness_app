package ca.unb.mobiledev.superduperfitnessapp

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var progress = findViewById<TextView>(R.id.progress)
        var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var currentVolume = audioManager.getStreamVolume((AudioManager.STREAM_MUSIC))
        seekBar = findViewById(R.id.seekBar)
        seekBar.max = maxVolume
        seekBar.progress = currentVolume

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progress.text = p1.toString()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, p1, 0)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }


}