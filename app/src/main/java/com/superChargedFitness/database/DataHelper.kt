package com.superChargedFitness.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.superChargedFitness.pojo.PWorkOutDetails
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DataHelper(val mContext: Context) {

    val DBName = "HomeWorkout.db"

    val workout_id = "Workout_id"
    val title = "Title"
    val videoLink = "videoLink"
    val descriptions = "Description"
    val time = "Time"
    val time_type = "time_type"
    val image = "Image"
    val tbl_youtube_link = "tbl_youtube_link"
    val youtube_link = "youtube_link"

    fun getReadWriteDB(): SQLiteDatabase {
        val dbFile = mContext.getDatabasePath(DBName)
        if (!dbFile.exists()) {
            try {
                val checkDB = mContext.openOrCreateDatabase(DBName, Context.MODE_PRIVATE, null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: Exception) {
                throw RuntimeException("Error creating source database", e)
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
    private fun copyDatabase(dbFile: File) {
        val `is` = mContext.assets.open(DBName)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (`is`.read(buffer) > 0) {
            os.write(buffer)
        }
        os.flush()
        os.close()
        `is`.close()
    }

    fun getWorkOutDetails(workoutTable: String): ArrayList<PWorkOutDetails> {

        val workdetails: ArrayList<PWorkOutDetails> = ArrayList()

        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From " + workoutTable
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val aClass = PWorkOutDetails()
                    aClass.workout_id = cursor.getInt(cursor.getColumnIndexOrThrow(workout_id))
                    aClass.title = cursor.getString(cursor.getColumnIndexOrThrow(title))
                    aClass.videoLink = cursor.getString(cursor.getColumnIndexOrThrow(videoLink))
                    aClass.descriptions = cursor.getString(cursor.getColumnIndexOrThrow(descriptions))
                    aClass.time = cursor.getString(cursor.getColumnIndexOrThrow(time))
                    aClass.time_type = cursor.getString(cursor.getColumnIndexOrThrow(time_type))
                    aClass.image = cursor.getString(cursor.getColumnIndexOrThrow(image))
                    workdetails.add(aClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen()) {
                db.close()
            }
        }
        return workdetails
    }

    fun getVideoLink(strWorkoutTitle: String): String {
        var strVideoLink = ""
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getReadWriteDB()
            val query = "Select * From " + tbl_youtube_link + " WHERE " + title + "='" + strWorkoutTitle+"'"
            cursor = db.rawQuery(query, null)
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    strVideoLink = cursor.getString(cursor.getColumnIndexOrThrow(youtube_link))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
            if (db != null && !db.isOpen()) {
                db.close()
            }
        }
        return strVideoLink
    }

}
