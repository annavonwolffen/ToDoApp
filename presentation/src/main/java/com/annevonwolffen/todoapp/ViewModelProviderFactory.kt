package com.annevonwolffen.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.annevonwolffen.todoapp.di.AppScope
import javax.inject.Inject
import javax.inject.Provider

@AppScope
class ViewModelProviderFactory @Inject constructor(
    private val creators: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException("model class $modelClass not found")
        return creator.get() as T
    }
}