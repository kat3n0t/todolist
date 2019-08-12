package com.todolist.mvp.models

import android.content.ContentValues
import com.todolist.database.*
import com.todolist.interfaces.INotesModel
import com.todolist.support.Note

class NotesModel(private val dbHelper: DBHelper) : INotesModel {

    override fun addNote(name: String) {
        val values = ContentValues()
        values.put(TABLE_NOTES_TEXT, name)

        val db = dbHelper.writableDatabase
        db.insert(TABLE_NOTES, null, values)
        db.close()
    }

    override fun changeCompleted(note: Note, isCompleted: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(TABLE_NOTES_IS_COMPLETED, isCompleted)
        val db = dbHelper.writableDatabase
        db.update(TABLE_NOTES, contentValues, "$TABLE_NOTES_ID = ${note.id}", null)
        db.close()
    }

    override fun renameNote(note: Note, newName: String) {
        val contentValues = ContentValues()
        contentValues.put(TABLE_NOTES_TEXT, newName)
        val db = dbHelper.writableDatabase
        db.update(TABLE_NOTES, contentValues, "$TABLE_NOTES_ID = ${note.id}", null)
        db.close()
    }

    override fun removeNote(note: Note) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_NOTES, "$TABLE_NOTES_ID = ${note.id}", null)
        db.close()
    }

    override fun getNote(id: Int): Note? {
        var note: Note? = null
        try {
            val query = "SELECT " +
                    "$TABLE_NOTES_ID, " +
                    "$TABLE_NOTES_TEXT, " +
                    "$TABLE_NOTES_IS_COMPLETED " +
                    "FROM $TABLE_NOTES " +
                    "WHERE $TABLE_NOTES_ID = $id;"
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
        var query = "SELECT " +
                "$TABLE_NOTES_ID, " +
                "$TABLE_NOTES_TEXT, " +
                "$TABLE_NOTES_IS_COMPLETED " +
                "FROM $TABLE_NOTES "
        query += if (haveCompletedNotes)
            "ORDER BY $TABLE_NOTES_IS_COMPLETED, $TABLE_NOTES_ID;" // Сперва по сортировке выдавать невыполненные задачи
        else
            "WHERE $TABLE_NOTES_IS_COMPLETED != 1;"
        return getNotes(query)
    }

    override fun getCompletedNotes(): LinkedHashSet<Note> {
        val query = "SELECT " +
                "$TABLE_NOTES_ID, " +
                "$TABLE_NOTES_TEXT, " +
                "$TABLE_NOTES_IS_COMPLETED " +
                "FROM $TABLE_NOTES " +
                "WHERE $TABLE_NOTES_IS_COMPLETED = 1;"
        return getNotes(query)
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
}