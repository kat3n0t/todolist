package com.devcom.todolist.mvp.models

import android.content.ContentValues
import com.devcom.todolist.database.*
import com.devcom.todolist.interfaces.INotesModel
import com.devcom.todolist.support.Note
import java.util.*
import kotlin.collections.LinkedHashSet

class NotesModel(private val dbHelper: DBHelper) : INotesModel {

    override fun addNote(name: String) {
        val values = ContentValues()
        values.put(NOTES_COLUMN_TEXT, name)

        val db = dbHelper.writableDatabase
        db.insert(NOTES_TABLE, null, values)
        db.close()
    }

    override fun addWorkersGuid(noteId: Int, guid: UUID) {
        val values = ContentValues()
        values.put(WORKERS_NOTE_ID, noteId)
        values.put(WORKERS_GUID, guid.toString())

        val db = dbHelper.writableDatabase
        db.insert(WORKERS_TABLE, null, values)
        db.close()
    }

    override fun changeCompleted(note: Note, isCompleted: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(NOTES_COLUMN_IS_COMPLETED, isCompleted)
        val db = dbHelper.writableDatabase
        db.update(NOTES_TABLE, contentValues, "$NOTES_COLUMN_ID = ${note.id}", null)
        db.close()
    }

    override fun changeNotification(noteId: Int, isCompleted: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(NOTES_COLUMN_IS_COMPLETED, isCompleted)
        val db = dbHelper.writableDatabase
        db.update(NOTES_TABLE, contentValues, "$NOTES_COLUMN_ID = $noteId", null)
        db.close()
    }

    override fun haveCompletedNotes(): Boolean {
        return getCompletedNotes().size > 0
    }

    override fun renameNote(note: Note, newName: String) {
        val contentValues = ContentValues()
        contentValues.put(NOTES_COLUMN_TEXT, newName)
        val db = dbHelper.writableDatabase
        db.update(NOTES_TABLE, contentValues, "$NOTES_COLUMN_ID = ${note.id}", null)
        db.close()
    }

    override fun removeNote(note: Note) {
        val db = dbHelper.writableDatabase
        db.delete(NOTES_TABLE, "$NOTES_COLUMN_ID = ${note.id}", null)
        db.close()
    }

    override fun removeWorkersGuid(note: Note) {
        val db = dbHelper.writableDatabase
        db.delete(WORKERS_TABLE, "$WORKERS_NOTE_ID = ${note.id}", null)
        db.close()
    }

    override fun getNote(id: Int): Note? {
        var note: Note? = null
        try {
            val query = "SELECT " +
                    "$NOTES_COLUMN_ID, " +
                    "$NOTES_COLUMN_TEXT, " +
                    "$NOTES_COLUMN_IS_COMPLETED " +
                    "FROM $NOTES_TABLE " +
                    "WHERE $NOTES_COLUMN_ID = $id;"
            val db = dbHelper.writableDatabase
            val c = db.rawQuery(query, null)
            if (c.moveToFirst()) {
                note = Note(c.getInt(0), c.getString(1), c.getInt(2) > 0)
            }
            c.close()
            db.close()
        } finally {
            return note
        }
    }

    override fun getNotes(haveCompletedNotes: Boolean): LinkedHashSet<Note> {
        val query = getNotesQuery(haveCompletedNotes)
        return getNotes(query)
    }

    private fun getNotesQuery(haveCompletedNotes: Boolean): String {
        var query = "SELECT " +
                "$NOTES_COLUMN_ID, " +
                "$NOTES_COLUMN_TEXT, " +
                "$NOTES_COLUMN_IS_COMPLETED " +
                "FROM $NOTES_TABLE "
        query += if (haveCompletedNotes)
            "ORDER BY $NOTES_COLUMN_IS_COMPLETED, $NOTES_COLUMN_ID;" // Сперва по сортировке выдавать невыполненные задачи
        else
            "WHERE $NOTES_COLUMN_IS_COMPLETED != 1;"
        return query
    }
    private fun getNotes(query: String): LinkedHashSet<Note> {
        val notes = LinkedHashSet<Note>()
        try {
            val db = dbHelper.readableDatabase
            val c = db.rawQuery(query, null)
            if (c.moveToFirst()) {
                do {
                    val id = c.getInt(0)
                    val name = c.getString(1)
                    val isCompleted = c.getInt(2) > 0
                    val note = Note(id, name, isCompleted)
                    notes.add(note)
                } while (c.moveToNext())
            }
            c.close()
            db.close()
        } finally {
            return notes
        }
    }

    override fun getCompletedNotes(): LinkedHashSet<Note> {
        val query = "SELECT " +
                "$NOTES_COLUMN_ID, " +
                "$NOTES_COLUMN_TEXT, " +
                "$NOTES_COLUMN_IS_COMPLETED " +
                "FROM $NOTES_TABLE " +
                "WHERE $NOTES_COLUMN_IS_COMPLETED = 1;"
        return getNotes(query)
    }

    override fun getWorkersGuid(noteId: Int): UUID? {
        val query = "SELECT " +
                "$WORKERS_GUID " +
                "FROM $WORKERS_TABLE " +
                "WHERE $WORKERS_NOTE_ID = $noteId;"
        var guid: UUID? = null
        try {
            val db = dbHelper.readableDatabase
            val c = db.rawQuery(query, null)
            if (c.moveToFirst()) {
                guid = UUID.fromString(c.getString(0))
            }
            c.close()
            db.close()
        } finally {
            return guid
        }
    }
}