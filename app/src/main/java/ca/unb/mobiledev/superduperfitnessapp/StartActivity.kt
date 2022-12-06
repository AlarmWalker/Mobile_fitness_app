package ca.unb.mobiledev.superduperfitnessapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.superduperfitnessapp.model.Sound
import ca.unb.mobiledev.superduperfitnessapp.util.JsonUtils

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val utils = JsonUtils(applicationContext)
        val sounds = utils.getSounds()

        val view = findViewById<RecyclerView>(R.id.recycler_view)

        val adp = MyAdapter(sounds, this)
        view.adapter = adp

        supportActionBar?.title = "Select sound"
        supportActionBar?.show()
    }

    fun returnView() : RecyclerView {
        return findViewById(R.id.recycler_view)
    }

    class MyAdapter(private val mDataset: ArrayList<Sound>, private val parentActivity: Activity) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private lateinit var player : MediaPlayer
        private var currentSound : String = ""
        private var paused = false

        class ViewHolder(var view: View) : RecyclerView.ViewHolder(
            view)

        // The inflate method of the LayoutInflater class can be used to obtain the
        // View object corresponding to an XML layout resource file. Here
        // onCreateViewHolder inflates the TextView corresponding to item_layout.xml
        // and uses it to instantiate a ViewHolder.
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sound, parent, false)
            return ViewHolder(v)
        }

        // onBindViewHolder binds a ViewHolder to the data at the specified
        // position in mDataset
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sound = mDataset[position]

            val title = holder.view.findViewById<TextView>(R.id.soundText)
            title.text = sound.description

            val image = holder.view.findViewById<ImageView>(R.id.soundImage)
            val res: Resources = parentActivity.resources
            val resId = res.getIdentifier(sound.soundImage, "drawable", parentActivity.packageName)
            image.setImageResource(resId)

            val button = holder.view.findViewById<ImageButton>(R.id.playButton)
            button.setOnClickListener {
                val fileName = sound.soundTitle
                audioPlayer(fileName.toString())
            }

            val confirmButton = holder.view.findViewById<Button>(R.id.confirmSound_button)

            confirmButton.setOnClickListener {
                val intent = Intent(parentActivity, RunningActivity::class.java)

                intent.putExtra("title",  sound.title)
                intent.putExtra("description", sound.description)
                intent.putExtra("soundTitle", sound.soundTitle)
                intent.putExtra("thumbnail", sound.thumbnail)

                val extras = intent.extras

                stopPlayer()

                try {
                    startActivity( title.context, intent, extras)
                } catch (ex: ActivityNotFoundException) {
                    Log.e("Intent", "Unable to load activity", ex)
                }
            }
        }

        private fun audioPlayer(fileName: String)
        {
            if (!this::player.isInitialized || currentSound != fileName) {

                if (this::player.isInitialized) {
                    player.stop()
                    player.reset()
                }

                val afd = parentActivity.assets.openFd(fileName)
                player = MediaPlayer()
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                player.prepare()
                player.setVolume(0.5f, 0.5f)

                currentSound = fileName
            }

            if (player.isPlaying) {
                player.pause()
                paused = true
            }
            else {
                player.start()
                paused = false
            }

            setIcons(fileName)
        }

        private fun stopPlayer() {
            if (this::player.isInitialized && player.isPlaying) {
                player.stop()
            }
            setIcons("")
        }

        private fun setIcons(fileName : String) {
            val res: Resources = parentActivity.resources
            val activityClass : StartActivity = parentActivity as StartActivity
            val holderView = activityClass.returnView()

            for (i in 0 until holderView.childCount) {
                val view = holderView.getChildAt(i)
                val viewButton = view.findViewById<ImageButton>(R.id.playButton)

                if (mDataset[i].soundTitle != fileName || (mDataset[i].soundTitle == fileName && paused)) {
                    viewButton.setImageResource(
                        res.getIdentifier(
                            "play_button",
                            "drawable",
                            parentActivity.packageName
                        )
                    )
                } else {
                    viewButton.setImageResource(
                        res.getIdentifier(
                            "pause_button",
                            "drawable",
                            parentActivity.packageName
                        )
                    )
                }
            }
        }

        override fun getItemCount(): Int {
            return mDataset.size
        }
    }
}