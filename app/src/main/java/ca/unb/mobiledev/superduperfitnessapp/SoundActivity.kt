package ca.unb.mobiledev.superduperfitnessapp

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SoundActivity : AppCompatActivity()
{
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

        val confirmButton = findViewById<Button>(R.id.confirm_button)

        confirmButton.setOnClickListener {
            val intent2 = Intent(this, RunningActivity::class.java)

            intent2.putExtra("title",  extras?.getString("title"))
            intent2.putExtra("description", extras?.getString("description"))

            val extras2 = intent2.extras

            try {
                startActivity(intent2, extras2)
            } catch (ex: ActivityNotFoundException) {
                Log.e("Intent", "Unable to load activity", ex)
            }
        }
    }
}