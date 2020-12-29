package com.example.pet3.presentation

import androidx.lifecycle.AndroidViewModel
import com.example.pet3.App
import io.reactivex.disposables.CompositeDisposable

abstract class MainViewModel(app: App) : AndroidViewModel(app) {
    val disposables = CompositeDisposable()

    abstract fun loadProgram()
    abstract fun saveProgram()
}