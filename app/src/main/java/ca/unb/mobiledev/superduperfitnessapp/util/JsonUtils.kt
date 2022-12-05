package ca.unb.mobiledev.superduperfitnessapp.util

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import ca.unb.mobiledev.superduperfitnessapp.model.Sound
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class JsonUtils(context: Context) {
    // TIP: lateinit is used to declare properties that
    // are guaranteed to be initialized in the future
    private lateinit var sounds: ArrayList<Sound>

    // Initializer (constructor) to read our data source (JSON file) into an array of course objects
    init {
        processJSON(context)
    }

    private fun processJSON(context: Context) {
        // Initialize the lateinit value
        sounds = ArrayList()
        try {
            // Create a JSON Object from file contents String
            val jsonObject = JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)))

            // Create a JSON Array from the JSON Object
            // This array is the "courses" array mentioned in the lab write-up
            val jsonArray = jsonObject.getJSONArray(KEY_SOUND)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val sound = Sound.Builder().id(obj.getString(KEY_SOUND_ID)).name(obj.getString(KEY_NAME)).description(obj.getString(KEY_DESCRIPTION)).soundTitle(obj.getString(
                    KEY_SOUND_TITLE)).soundImage(obj.getString(KEY_SOUND_IMAGE)).thumbnail(obj.getString(KEY_THUMBNAIL)).build()

                sounds.add(i, sound)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAssets(context: Context): String? {
        var string: String? = ""

        try {
            val assetMgr = context.assets
            val inputStream: InputStream = assetMgr.open(SOUND_JSON_FILE)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            string = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return string
    }

    // Getter method for courses ArrayList
    fun getSounds(): ArrayList<Sound> {
        return sounds
    }

    companion object {
        private const val SOUND_JSON_FILE = "sounds.json"
        private const val KEY_SOUND = "sounds"
        private const val KEY_SOUND_ID = "soundID"
        private const val KEY_NAME = "name"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_SOUND_TITLE = "soundTitle"
        private const val KEY_SOUND_IMAGE = "soundImage"
        private const val KEY_THUMBNAIL = "thumbnail"
    }
}