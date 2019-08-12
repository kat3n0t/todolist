package com.todolist.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal const val TABLE_NOTES = "notes"
internal const val TABLE_NOTES_ID = "_id"
internal const val TABLE_NOTES_TEXT = "text"
internal const val TABLE_NOTES_IS_COMPLETED = "isCompleted"

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(
            "CREATE TABLE $TABLE_NOTES (\n" +
                    "$TABLE_NOTES_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "$TABLE_NOTES_TEXT TEXT,\n" +
                    "$TABLE_NOTES_IS_COMPLETED INTEGER DEFAULT 0\n" +
                    ");"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(p0)
    }
}