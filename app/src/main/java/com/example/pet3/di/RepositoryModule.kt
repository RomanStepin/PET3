package com.example.pet3.di

import com.example.pet3.App
import com.example.pet3.repository.Repository
import com.example.pet3.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule(var app: App) {

    @Provides
    @Singleton
    public fun getRepository(): Repository {
        return RepositoryImpl(app = app)
    }
}