package com.todolist.interfaces

interface INoteView {
    fun getEditingNoteId() : Int?
    fun getNoteText(): String
    fun showDeleteButton(isShow : Boolean)
    fun setViewTitle(newTitle: Int)
    fun setText(text: String)
}