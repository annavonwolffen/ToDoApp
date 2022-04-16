package com.annevonwolffen.domain.settings

interface SettingsInteractor {
    fun doneTasksVisibility(): Boolean
    fun setDoneTasksVisibility(visible: Boolean)
}