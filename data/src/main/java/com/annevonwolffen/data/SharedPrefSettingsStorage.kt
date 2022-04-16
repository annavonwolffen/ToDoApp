package com.annevonwolffen.data

import android.content.Context
import android.content.SharedPreferences
import com.annevonwolffen.domain.settings.SettingsStorage

class SharedPrefSettingsStorage(context: Context) : SettingsStorage {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)

    override fun doneTasksVisibility(): Boolean =
        sharedPreferences.getBoolean(DONE_TASKS_VISIBILITY_PREF, false)

    override fun setDoneTasksVisibility(visible: Boolean) {
        sharedPreferences.edit().putBoolean(DONE_TASKS_VISIBILITY_PREF, visible).apply()
    }

    private companion object {
        const val SETTINGS_PREF = "SETTINGS_PREF"
        const val DONE_TASKS_VISIBILITY_PREF = "DONE_TASKS_VISIBILITY_PREF"
    }
}