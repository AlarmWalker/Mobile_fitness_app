package ca.unb.mobiledev.superduperfitnessapp.util

import android.content.Context
import android.location.Location
import android.util.Log
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class LocationJsonUtils(context: Context) {
    private lateinit var locations: ArrayList<Location>

    init {
        processJSON(context)
    }

    private fun processJSON(context: Context) {
        locations = ArrayList()
        try {
            val jsonObject = JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)))
            val jsonArray = jsonObject.getJSONArray(KEY_LOCATION)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val location = Location(obj.getString("locationID"))
                location.latitude = obj.getString("latitude").toDouble()
                location.longitude = obj.getString("longitude").toDouble()

                locations.add(i, location)

                Log.e("Location", "Location: " + i + "\n\tLat:\t" + location.latitude + "\n\tLong:\t" + location.longitude)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAssets(context: Context): String? {
        var string: String? = ""

        try {
            val assetMgr = context.assets
            val inputStream: InputStream = assetMgr.open(LOCATION_JSON_FILE)
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
    fun getLocations(): ArrayList<Location> {
        return locations
    }

    companion object {
        private const val LOCATION_JSON_FILE = "location.json"
        private const val KEY_LOCATION = "locations"
    }
}