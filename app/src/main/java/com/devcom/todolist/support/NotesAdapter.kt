package com.devcom.todolist.support

import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.devcom.todolist.R
import com.devcom.todolist.interfaces.INotesPresenter
import com.devcom.todolist.mvp.presenters.NotesPresenter
import com.devcom.todolist.mvp.views.MainActivity

class NotesAdapter(private val context: Context, private var notesSet: LinkedHashSet<Note>) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return NotesViewHolder(view, NotesPresenter(context as MainActivity))
    }

    override fun getItemCount(): Int {
        return notesSet.count()
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notesSet.elementAt(position))
    }

    inner class NotesViewHolder(itemView: View, private val presenter: INotesPresenter) :
        RecyclerView.ViewHolder(itemView) {

        private val listItemNoteView: CheckBox = itemView.findViewById(R.id.checkBox_note)

        fun bind(note: Note) {
            listItemNoteView.setOnLongClickListener {
                presenter.onItemLongClicked(listItemNoteView, note)
                true
            }
            setItemStyle(listItemNoteView, note)
        }

        private fun setItemStyle(itemView: CheckBox, note: Note) {
            itemView.text = note.name

            itemView.isChecked = note.isCompleted
            setCheckBoxCompleted(note, note.isCompleted) // предварительное обновление

            itemView.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    moveDown(note)
                else
                    moveUp(note)
                presenter.onItemClicked(this, note, isChecked)
            }
        }

        private fun moveUp(note: Note) {
            layoutPosition.takeIf { it > 0 }?.also { currentPosition ->
                val newSet = LinkedHashSet<Note>()
                newSet.add(note)
                notesSet.remove(note)
                newSet.addAll(notesSet)
                notesSet = newSet
                notifyItemMoved(currentPosition, 0)
            }
        }

        private fun moveDown(note: Note) {
            layoutPosition.takeIf { it < itemCount - 1 }?.also { currentPosition ->
                notesSet.remove(note).also {
                    notesSet.add(note)
                }
                notifyItemMoved(currentPosition, itemCount - 1)
            }
        }

        fun setCheckBoxCompleted(note: Note, isCompleted: Boolean) {
            if (isCompleted) {
                listItemNoteView.alpha = 0.5f // делает элемент полупрозрачным

                val spannableString = SpannableString(note.name)
                spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, 0)
                listItemNoteView.text = spannableString // перечеркивает текст заметки
            } else {
                listItemNoteView.text = note.name
                listItemNoteView.alpha = 1.0f
            }
        }
    }
}