package com.devcom.todolist.interfaces

interface IEditNotePresenter : INotePresenter {
    fun onDeleteClick()
    fun onNotificationClick(isNotificationOn: Boolean)
}