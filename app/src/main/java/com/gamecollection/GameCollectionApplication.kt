package com.gamecollection

import android.app.Application
import com.gamecollection.di.AppContainer

class GameCollectionApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
