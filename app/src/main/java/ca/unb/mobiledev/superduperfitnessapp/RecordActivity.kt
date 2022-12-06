package ca.unb.mobiledev.superduperfitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.superduperfitnessapp.db.db
import ca.unb.mobiledev.superduperfitnessapp.db.db2

class RecordActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: RecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_activity)

        recyclerView = findViewById(R.id.recordRecycler)
        initRecyclerView()
        getRecord()
    }

    private fun getRecord() {
        val rcdList = db2(applicationContext).getRecord()
        for (i in rcdList.indices){
            rcdList[i].image = db(applicationContext).getBitmapByName(rcdList[i].name)
        }
        adapter?.addItems(rcdList)
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordAdapter()
        recyclerView.adapter = adapter
    }
}