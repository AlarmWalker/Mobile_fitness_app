package ca.unb.mobiledev.superduperfitnessapp
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.superduperfitnessapp.db.db
import ca.unb.mobiledev.superduperfitnessapp.util.dbUtil

class SettingActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var audioManager: AudioManager
    private lateinit var sharedPref: SharedPreferences
    private lateinit var profilePic: ImageView
    var mainActivity = MainActivity()

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
        var saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener{
            val bitmap = (profilePic.drawable as BitmapDrawable).bitmap

            if(!TextUtils.isEmpty(nicknameText.text.toString())){
                db(applicationContext).addBitmap(nicknameText.text.toString(), dbUtil.getBytes(bitmap))
                Toast.makeText(this@SettingActivity, "Saved!", Toast.LENGTH_SHORT)
            }
            else{
                Toast.makeText(this@SettingActivity, "please fill in nickname!", Toast.LENGTH_SHORT)
            }
            mainActivity.savedName = sharedPref.getString("name", "default value") as String
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val pickedImage = it.data?.data
                profilePic.setImageURI(pickedImage)
            }
        }

}