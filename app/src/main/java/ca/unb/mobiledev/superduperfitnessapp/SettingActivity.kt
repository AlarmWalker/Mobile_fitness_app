package ca.unb.mobiledev.superduperfitnessapp
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.superduperfitnessapp.db.db
import ca.unb.mobiledev.superduperfitnessapp.util.dbUtil
import java.lang.Exception
import java.util.*

class SettingActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var audioManager: AudioManager
    private lateinit var sharedPref: SharedPreferences
    private lateinit var profilePic: ImageView
    private lateinit var saveButton: Button

    private var prefs : SharedPreferences? = null
    private lateinit var speedBar: SeekBar
    private lateinit var speedText: TextView
    private lateinit var speedHelperText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)

        supportActionBar?.title = "Settings"
        supportActionBar?.show()

        initSharedPreferences()
        speedBar = findViewById(R.id.speedSelection)
        speedText = findViewById(R.id.speedText)
        speedHelperText = findViewById(R.id.speedHelperText)

        speedBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                speedHelperText.text = (p1+1).toString() + " m/s"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })



        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        var progress = findViewById<TextView>(R.id.progress)
        var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var currentVolume = audioManager.getStreamVolume((AudioManager.STREAM_MUSIC))
        seekBar = findViewById(R.id.seekBar)
        seekBar.max = maxVolume
        seekBar.progress = currentVolume

        sharedPref =getSharedPreferences("addName", Context.MODE_PRIVATE)
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

        var uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener{
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            getResult.launch(photoPicker)
        }

        var nicknameText = findViewById<EditText>(R.id.nickname)
        profilePic = findViewById<ImageView>(R.id.profilePic)
        saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener{
            try {
                val bitmap = (profilePic.drawable as BitmapDrawable).bitmap
                if(!TextUtils.isEmpty(nicknameText.text.toString())){
                    db(applicationContext).addBitmap(nicknameText.text.toString(), dbUtil.getBytes(bitmap))
                    Toast.makeText(this@SettingActivity, "Saved!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this@SettingActivity, "please fill in nickname!", Toast.LENGTH_SHORT).show()
                }
            }
            catch(ex: Exception) {
                Log.e("saveButton", "Error when saving")
                Toast.makeText(this@SettingActivity, "Please upload a profile picture!", Toast.LENGTH_SHORT).show()
            }

            writeToSharedPrefs(speedBar.progress+1)
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val pickedImage = it.data?.data
                profilePic.setImageURI(pickedImage)
                saveButton.isEnabled = true
            }
        }



    private fun writeToSharedPrefs(speed: Int) {
        val editor = prefs!!.edit()
        editor.putInt(SPEED_KEY, speed)
        editor.apply()
    }

    private fun initSharedPreferences() {
        prefs = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_FILE_NAME = "AppPrefs"
        private const val SPEED_KEY = "SPEED_KEY"
    }

}