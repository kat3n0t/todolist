package com.todolist.database

import android.content.Context
import com.todolist.interfaces.INotesModel
import com.todolist.mvp.models.NotesModel

class DBManager(context : Context) {

    var notesModel : INotesModel

    init {
        val dbHelper = DBHelper(context, "database", null, 1)
        notesModel = NotesModel(dbHelper)
    }
}

