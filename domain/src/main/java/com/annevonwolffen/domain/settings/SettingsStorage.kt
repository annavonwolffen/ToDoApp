package com.annevonwolffen.domain.settings

interface SettingsStorage {
    fun doneTasksVisibility(): Boolean
    fun setDoneTasksVisibility(visible: Boolean)
}