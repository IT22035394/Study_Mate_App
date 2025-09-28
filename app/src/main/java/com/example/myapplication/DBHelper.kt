package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.util.getColumnIndexOrThrow

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "signup.db"
        private const val DATABASE_VERSION = 3 // bump version for schema changes

        // ---------- USERS TABLE ----------
        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_FIRST = "first_name"
        private const val COL_LAST = "last_name"
        private const val COL_EMAIL = "email"
        private const val COL_PHONE = "phone"
        private const val COL_PASSWORD = "password"

        // ---------- VIDEOS TABLE ----------
        private const val TABLE_VIDEOS = "videos"
        private const val COL_VIDEO_ID = "video_id"
        private const val COL_VIDEO_NAME = "video_name"
        private const val COL_VIDEO_DESC = "video_description"
        private const val COL_VIDEO_URI = "video_uri"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Users table with constraints
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FIRST TEXT NOT NULL,
                $COL_LAST TEXT NOT NULL,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PHONE TEXT UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            );
        """.trimIndent()
        db?.execSQL(createUsersTable)


        // Videos table
        val createVideosTable = ("CREATE TABLE $TABLE_VIDEOS (" +
                "$COL_VIDEO_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_VIDEO_NAME TEXT," +
                "$COL_VIDEO_DESC TEXT," +
                "$COL_VIDEO_URI TEXT)")
        db?.execSQL(createVideosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_VIDEOS")
        onCreate(db)
    }

    // ---------- USER METHODS ----------
    fun insertUser(first: String, last: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_FIRST, first)
            put(COL_LAST, last)
            put(COL_EMAIL, email)
            put(COL_PHONE, phone)
            put(COL_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result != -1L
    }

    fun checkUser(firstName: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COL_FIRST = ? AND $COL_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(firstName, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // ---------- VIDEO METHODS ----------
    fun insertVideo(name: String, description: String, uri: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_VIDEO_NAME, name)
            put(COL_VIDEO_DESC, description)
            put(COL_VIDEO_URI, uri)
        }
        val result = db.insert(TABLE_VIDEOS, null, contentValues)
        db.close()
        return result != -1L
    }

    fun getAllVideos(): List<Map<String, String>> {
        val videos = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_VIDEOS ORDER BY $COL_VIDEO_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val video = mapOf(
                    COL_VIDEO_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COL_VIDEO_ID)).toString(),
                    COL_VIDEO_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COL_VIDEO_NAME)),
                    COL_VIDEO_DESC to cursor.getString(cursor.getColumnIndexOrThrow(COL_VIDEO_DESC)),
                    COL_VIDEO_URI to cursor.getString(cursor.getColumnIndexOrThrow(COL_VIDEO_URI))
                )
                videos.add(video)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return videos
    }

    // Delete video
    fun deleteVideo(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_VIDEOS, "$COL_VIDEO_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // Update video
    fun updateVideo(id: Int, name: String, desc: String, uri: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_VIDEO_NAME, name)
            put(COL_VIDEO_DESC, desc)
            put(COL_VIDEO_URI, uri)
        }
        val result = db.update(TABLE_VIDEOS, contentValues, "$COL_VIDEO_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
