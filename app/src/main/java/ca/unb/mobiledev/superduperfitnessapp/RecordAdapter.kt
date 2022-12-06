package ca.unb.mobiledev.superduperfitnessapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.superduperfitnessapp.model.Record
import ca.unb.mobiledev.superduperfitnessapp.util.dbUtil

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>(){

    private var rcdList: ArrayList<Record> = ArrayList()

    fun addItems(items: ArrayList<Record>){
        this.rcdList = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
    )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val rcd = rcdList[position]
        holder.bindView(rcd)
    }

    override fun getItemCount(): Int {
        return rcdList.size
    }

    class RecordViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var medal = view.findViewById<ImageView>(R.id.medal)
        private var name = view.findViewById<TextView>(R.id.userName)
        private var record = view.findViewById<TextView>(R.id.record)

        fun bindView(rcd: Record){
            name.text = rcd.name
            record.text = rcd.data.toString()
            val bitmap: Bitmap = dbUtil.getImage(rcd.image!!)
            medal.setImageBitmap(bitmap)
        }
    }

}