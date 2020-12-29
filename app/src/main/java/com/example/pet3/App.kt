package com.example.pet3

import android.app.Application
import com.example.pet3.di.*
import java.util.*

class App: Application() {

    companion object {
        lateinit var udpServiceComponent: UdpServiceComponent
        var STATE: States = States.HEARTBEAT
        lateinit var instance: App
    }

    init {
        udpServiceComponent = DaggerUdpServiceComponent.builder().udpServiceModule(UdpServiceModule(this)).build()
        instance = this
    }



    override fun onCreate() {
        super.onCreate()


    }
}