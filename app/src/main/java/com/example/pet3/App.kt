package com.example.pet3

import android.app.Application
import com.example.pet3.di.*
import java.util.*

class App: Application() {

    companion object {
        lateinit var udpServiceComponent: UdpServiceComponent
        lateinit var repositoryComponent: RepositoryComponent
        var STATE: States = States.HEARTBEAT
        lateinit var instance: App
    }

    init {
        udpServiceComponent = DaggerUdpServiceComponent.builder().udpServiceModule(UdpServiceModule(this)).build()
        repositoryComponent = DaggerRepositoryComponent.builder().repositoryModule(RepositoryModule(this)).build()
        instance = this
    }



    override fun onCreate() {
        super.onCreate()


    }
}