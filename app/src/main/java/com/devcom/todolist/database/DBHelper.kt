package com.devcom.todolist.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal const val NOTES_TABLE = "notes"
internal const val NOTES_COLUMN_ID = "_id"
internal const val NOTES_COLUMN_TEXT = "text"
internal const val NOTES_COLUMN_IS_COMPLETED = "is_completed"

internal const val WORKERS_TABLE = "note_workers"
internal const val WORKERS_NOTE_ID = "note_id"
internal const val WORKERS_GUID = "worker_guid"

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        if (p0 != null) {
            p0.execSQL(
                "CREATE TABLE $NOTES_TABLE (\n" +
                        "$NOTES_COLUMN_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "$NOTES_COLUMN_TEXT TEXT,\n" +
                        "$NOTES_COLUMN_IS_COMPLETED INTEGER DEFAULT 0);"
            )
            p0.execSQL(
                "CREATE TABLE $WORKERS_TABLE (\n" +
                        "$WORKERS_NOTE_ID INTEGER NOT NULL UNIQUE,\n" +
                        "$WORKERS_GUID TEXT NOT NULL UNIQUE,\n" +
                        "PRIMARY KEY ($WORKERS_NOTE_ID, $WORKERS_GUID)\n" +
                        "FOREIGN KEY ($WORKERS_NOTE_ID) REFERENCES $NOTES_TABLE($NOTES_COLUMN_ID) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE);"
            )
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        if (p0 != null) {
            p0.execSQL("DROP TABLE IF EXISTS $NOTES_TABLE;")
            p0.execSQL("DROP TABLE IF EXISTS $WORKERS_TABLE;")
        }
        onCreate(p0)
    }
}