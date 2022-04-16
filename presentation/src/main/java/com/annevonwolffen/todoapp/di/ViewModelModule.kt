package com.annevonwolffen.todoapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.annevonwolffen.todoapp.TasksViewModel
import com.annevonwolffen.todoapp.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    abstract fun provideTasksViewModel(viewModel: TasksViewModel): ViewModel
}
