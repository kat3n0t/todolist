package com.todolist.interfaces

import com.todolist.support.Note

interface INotesModel {
    fun addNote(name: String)
    fun changeCompleted(note: Note, isCompleted: Boolean)
    fun changeNotification(noteId: Int, isCompleted: Boolean)
    fun renameNote(note: Note, newName : String)
    fun removeNote(note: Note)
    fun getNote(id: Int): Note?
    fun getCompletedNotes() : LinkedHashSet<Note>
    fun getNotes(haveCompletedNotes: Boolean): LinkedHashSet<Note>
}