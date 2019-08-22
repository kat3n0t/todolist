package com.todolist.interfaces

import com.todolist.support.Note
import java.util.*
import kotlin.collections.LinkedHashSet

interface INotesModel {
    fun addNote(name: String)
    fun addWorkersGuid(noteId: Int, guid: UUID)
    fun changeCompleted(note: Note, isCompleted: Boolean)
    fun changeNotification(noteId: Int, isCompleted: Boolean)
    fun getNote(id: Int): Note?
    fun getCompletedNotes() : LinkedHashSet<Note>
    fun getNotes(haveCompletedNotes: Boolean): LinkedHashSet<Note>
    fun getWorkersGuid(noteId: Int) : UUID?
    fun renameNote(note: Note, newName : String)
    fun removeNote(note: Note)
    fun removeWorkersGuid(note: Note)
}