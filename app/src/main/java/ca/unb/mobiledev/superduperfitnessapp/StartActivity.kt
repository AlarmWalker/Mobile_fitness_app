package ca.unb.mobiledev.superduperfitnessapp

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.superduperfitnessapp.model.Sound
import ca.unb.mobiledev.superduperfitnessapp.util.JsonUtils
import org.json.JSONObject
import java.util.ArrayList

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val utils = JsonUtils(applicationContext)
        val sounds = utils.getSounds()

        val view = findViewById<RecyclerView>(R.id.recycler_view)

        val adp = MyAdapter(sounds, this)
        view.adapter = adp
    }

    class MyAdapter(private val mDataset: ArrayList<Sound>, private val parentActivity: Activity) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        //final lateinit var course : Course

        // ViewHolder represents an individual item to display. In this case
        // it will just be a single TextView (displaying the title of a course)
        // but RecyclerView gives us the flexibility to do more complex things
        // (e.g., display an image and some text).
        class ViewHolder(var mTextView: TextView) : RecyclerView.ViewHolder(
            mTextView)

        // The inflate method of the LayoutInflater class can be used to obtain the
        // View object corresponding to an XML layout resource file. Here
        // onCreateViewHolder inflates the TextView corresponding to item_layout.xml
        // and uses it to instantiate a ViewHolder.
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false) as TextView
            return ViewHolder(v)
        }

        // onBindViewHolder binds a ViewHolder to the data at the specified
        // position in mDataset
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sound = mDataset[position]
            holder.mTextView.text = sound.title

            holder.mTextView.setOnClickListener {
                val intent = Intent(parentActivity, SoundActivity::class.java)

                intent.putExtra("title",  sound.title)
                intent.putExtra("description", sound.description)
                intent.putExtra("soundTitle", sound.soundTitle)

                val extras = intent.extras

                try {
                    startActivity( holder.mTextView.context, intent, extras)
                } catch (ex: ActivityNotFoundException) {
                    Log.e("Intent", "Unable to load activity", ex)
                }
            }
        }

        override fun getItemCount(): Int {
            return mDataset.size
        }
    }
}