package ca.unb.mobiledev.superduperfitnessapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import ca.unb.mobiledev.superduperfitnessapp.model.Record
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.lang.Exception

class db2(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null, DB_VER) {

    @Throws(SQLiteException::class)
    fun addRecord(name: String, data: Long): Long{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_NAME, name)
        cv.put(COL_DATA, data)

        val success = db.insertOrThrow(TABLE_NAME2, null, cv)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getRecord(): ArrayList<Record>{
        val recordList: ArrayList<Record> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NAME2 ORDER BY Data DESC LIMIT 5"
        val db = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch(e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var name: String
        var data: Long

        if(cursor.moveToFirst()){
            do{
                name = cursor.getString(cursor.getColumnIndex("Name"))
                data = cursor.getLong(cursor.getColumnIndex("Data"))
                val record = Record(name = name, data = data)
                recordList.add(record)
            }while (cursor.moveToNext())
        }
        return recordList
    }

    companion object {
        private const val DATABASE_NAME = "111.db"
        private const val DB_VER=1
        private const val COL_NAME="Name"
        private const val COL_DATA = "Data"
        private const val TABLE_NAME2 = "Record"
    }

}