package com.annevonwolffen.todoapp.utils

import java.util.Date
import java.util.Calendar

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Date.toCalendar(hour: Int, minutes: Int, seconds: Int): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, seconds)
    return calendar
}

fun Calendar.toDate(): Date = time
