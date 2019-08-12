package com.todolist.mvp.views

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        when (intent.getIntExtra(NOTE_MODE, CREATE_NEW_NOTE)) {
            CREATE_NEW_NOTE -> notePresenter = NewNotePresenter(this)
            EDIT_NOTE -> notePresenter = EditNotePresenter(this)
        }

        setButtonListeners()

        // TODO: Добавить напоминание
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
    }

    override fun getNoteText(): String {
        return editText_note.text.toString()
    }
}