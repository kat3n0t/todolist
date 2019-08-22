package com.todolist.mvp.views

import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.todolist.R
import com.todolist.interfaces.IEditNotePresenter
import com.todolist.interfaces.INotePresenter
import com.todolist.interfaces.INoteView
import com.todolist.mvp.presenters.EditNotePresenter
import com.todolist.mvp.presenters.NewNotePresenter
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity(), INoteView {

    private lateinit var notePresenter: INotePresenter

    // Ресурс изображения с кнопки уведомления
    // По умолчанию уведомление должно быть выключено
    private var imageSource: Int = R.drawable.ic_notifications_off

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        when (intent.getIntExtra(NOTE_MODE, CREATE_NEW_NOTE)) {
            CREATE_NEW_NOTE -> notePresenter = NewNotePresenter(this)
            EDIT_NOTE -> notePresenter = EditNotePresenter(this)
        }

        setButtonListeners()
    }

    override fun onStart() {
        super.onStart()
        notePresenter.onStart()
    }

    override fun getEditingNoteId(): Int? {
        val id = intent.getIntExtra(NOTE_ID, 0)
        return if (id != 0)
            id
        else
            null
    }

    override fun setViewTitle(newTitle: Int) {
        title = getString(newTitle)
    }

    override fun setText(text: String) {
        editText_note.setText(text)
    }

    override fun setImageButtonType(type: Int) {
        try {
            imageSource = type
            imageButton_notification.setImageResource(imageSource)
            imageButton_notification.visibility = View.VISIBLE
        } catch (e: Exception) {
            imageButton_notification.visibility = View.GONE
        }
    }

    override fun showDeleteButton(isShow: Boolean) {
        if (!isShow)
            btn_delete.visibility = View.GONE
    }

    private fun setButtonListeners() {
        btn_save.setOnClickListener {
            notePresenter.onSaveClick()
        }
        btn_delete.setOnClickListener {
            (notePresenter as? IEditNotePresenter)?.onDeleteClick()
        }
        imageButton_notification.setOnClickListener {
            (notePresenter as? IEditNotePresenter)?.onNotificationClick(imageSource == R.drawable.ic_notifications_off)
        }
    }

    override fun getNoteText(): String {
        return editText_note.text.toString()
    }

    override fun onDestroy() {
        notePresenter.onDestroy()
        super.onDestroy()
    }
}