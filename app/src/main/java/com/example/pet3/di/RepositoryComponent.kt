package com.example.pet3.di

import com.example.pet3.repository.Repository
import dagger.Component
import javax.inject.Singleton



@Component(modules = [RepositoryModule::class, AppModule::class])
@Singleton
public interface RepositoryComponent {
    fun getRepository(): Repository
}