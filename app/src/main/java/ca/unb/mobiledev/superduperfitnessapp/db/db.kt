package ca.unb.mobiledev.superduperfitnessapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class db(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null, DB_VER) {

    @Throws(SQLiteException::class)
    fun addBitmap(name:String, image:ByteArray){
        val database = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_NAME, name)
        cv.put(COL_DATA, image)
        database.insertOrThrow(TABLE_NAME, null, cv)
        Log.i("db", "db updated")
    }

    @SuppressLint("Range")
    fun getBitmapByName(name:String): ByteArray? {
        val db = this.writableDatabase
        val qb = SQLiteQueryBuilder()

        val sqlSelect = arrayOf(COL_DATA)

        qb.tables = TABLE_NAME
        val c = qb.query(db,sqlSelect, "Name = ?", arrayOf(name), null, null, null)

        var result:ByteArray?=null
        if(c.moveToFirst()){
            do {
                result = c.getBlob(c.getColumnIndex(COL_DATA))
            } while(c.moveToNext())
        }
        return result
    }

    companion object {
        private const val DATABASE_NAME = "SaveBitmap.db"
        private const val DB_VER=1
        private const val TABLE_NAME = "Gallery"
        private const val COL_NAME="Name"
        private const val COL_DATA = "Data"
    }

}