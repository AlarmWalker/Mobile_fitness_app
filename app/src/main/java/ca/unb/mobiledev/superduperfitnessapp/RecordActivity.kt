package ca.unb.mobiledev.superduperfitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.superduperfitnessapp.db.db2

class RecordActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: RecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_activity)

        initRecyclerView()
        getRecord()
    }

    private fun getRecord() {
        val rcdList = db2(applicationContext).getRecord()
        adapter?.addItems(rcdList)
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordAdapter()
        recyclerView.adapter = adapter
    }
}