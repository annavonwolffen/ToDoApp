package com.annevonwolffen.todoapp.utils

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.annevonwolffen.todoapp.R
import com.annevonwolffen.todoapp.TasksAdapter
import com.annevonwolffen.todoapp.model.TaskPresentationModel
import java.util.Date
import java.util.Calendar

@BindingAdapter("visibility")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("tasksData")
fun RecyclerView.setTasks(tasks: List<TaskPresentationModel>?) {
    tasks?.takeIf { adapter is TasksAdapter }?.let {
        (adapter as TasksAdapter).submitList(tasks)
    }
}

@BindingAdapter("isCrossedOut")
fun TextView.setCrossedOut(isCrossedOutNeeded: Boolean) {
    paintFlags = if (isCrossedOutNeeded) {
        paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG.inv())
    }
}

@BindingAdapter("stringFromDate")
fun TextView.setStringFromDate(date: Date?) {
    this.text = date?.let {
        val calendar = it.toCalendar()
        resources.getString(
            R.string.deadline_date_format,
            calendar.get(Calendar.DAY_OF_MONTH),
            resources.getStringArray(R.array.months_genitive)[calendar.get(Calendar.MONTH)],
            calendar.get(Calendar.YEAR)
        )
    } ?: ""
}