package com.example.pet3.di

import android.content.Context
import com.example.pet3.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Provides
    @Singleton
    fun getMyApp(): App = app

    @Provides
    @Singleton
    fun getContext(): Context = app
}