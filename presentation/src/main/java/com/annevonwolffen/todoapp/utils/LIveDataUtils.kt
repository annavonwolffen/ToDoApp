package com.annevonwolffen.todoapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T1, T2, R> combine(
    liveData1: LiveData<T1>, liveData2: LiveData<T2>, merge: (value1: T1?, value2: T2?) -> R
): LiveData<R> = MediatorLiveData<R>().apply {
    addSource(liveData1) {
        value = merge(it, liveData2.value)
    }
    addSource(liveData2) {
        value = merge(liveData1.value, it)
    }
}

fun <T1, T2, R> Pair<LiveData<T1>, LiveData<T2>>.map(merge: (value1: T1?, value2: T2?) -> R) =
    combine(first, second, merge)