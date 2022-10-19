package ca.unb.mobiledev.superduperfitnessapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
    }
}