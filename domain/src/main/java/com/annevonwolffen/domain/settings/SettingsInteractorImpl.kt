package com.annevonwolffen.domain.settings

class SettingsInteractorImpl(private val settingsStorage: SettingsStorage) : SettingsInteractor {
    override fun doneTasksVisibility(): Boolean = settingsStorage.doneTasksVisibility()

    override fun setDoneTasksVisibility(visible: Boolean) {
        settingsStorage.setDoneTasksVisibility(visible)
    }
}