package com.example.pet3.di

import com.example.pet3.domain.udp.UdpService
import dagger.Component
import javax.inject.Singleton


@Component(modules = [AppModule::class, UdpServiceModule::class])
@Singleton
interface UdpServiceComponent {
    fun getUdpService(): UdpService
}