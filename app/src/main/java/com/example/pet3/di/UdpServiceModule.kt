package com.example.pet3.di

import com.example.pet3.App
import com.example.pet3.domain.udp.UdpService
import com.example.pet3.domain.udp.UdpServiceImpl
import dagger.Module
import dagger.Provides
import java.net.DatagramSocket
import javax.inject.Singleton


@Module
class UdpServiceModule(var app: App) {

    @Provides
    @Singleton
    fun getUdpService(): UdpService = UdpServiceImpl(app, getSocket())

    private fun getSocket() = DatagramSocket(4096).also { it.broadcast = true }
}