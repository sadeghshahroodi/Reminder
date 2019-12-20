package com.sadegh.reminder

import android.app.Application
import android.content.Context
import android.util.Log
import com.sadegh.reminder.io.DBHelper
import com.sadegh.reminder.util.Util

class App : Application() {
    companion object {
        lateinit var instance: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DBHelper(this)
        Util.checkAlarmManger(this)
    }
}