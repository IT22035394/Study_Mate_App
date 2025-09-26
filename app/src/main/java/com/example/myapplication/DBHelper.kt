package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.util.getColumnIndexOrThrow
import android.database.Cursor


class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "signup.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_FIRST = "first_name"
        private const val COL_LAST = "last_name"
        private const val COL_EMAIL = "email"
        private const val COL_PHONE = "phone"
        private const val COL_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_USERS (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_FIRST TEXT," +
                "$COL_LAST TEXT," +
                "$COL_EMAIL TEXT," +
                "$COL_PHONE TEXT," +
                "$COL_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(first: String, last: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_FIRST, first)
        contentValues.put(COL_LAST, last)
        contentValues.put(COL_EMAIL, email)
        contentValues.put(COL_PHONE, phone)
        contentValues.put(COL_PASSWORD, password)

        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result != -1L
    }

    // Read all users
    fun getAllUsers(): List<Map<String, String>> {
        val userList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    COL_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)).toString(),
                    COL_FIRST to cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST)),
                    COL_LAST to cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST)),
                    COL_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    COL_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    COL_PASSWORD to cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    // Update user by ID
    fun updateUser(id: Int, first: String, last: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_FIRST, first)
            put(COL_LAST, last)
            put(COL_EMAIL, email)
            put(COL_PHONE, phone)
            put(COL_PASSWORD, password)
        }
        val result = db.update(TABLE_USERS, contentValues, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // Delete user by ID
    fun deleteUser(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USERS, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    //check login details
    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


}


