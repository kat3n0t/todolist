package com.devcom.todolist.database

import android.content.Context
import com.devcom.todolist.interfaces.INotesModel
import com.devcom.todolist.mvp.models.NotesModel

class DBManager(context : Context) {

    var notesModel : INotesModel

    init {
        val dbHelper = DBHelper(context, "database", null, 1)
        notesModel = NotesModel(dbHelper)
    }
}