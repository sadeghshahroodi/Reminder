package com.sadegh.reminder.io

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.sadegh.reminder.App
import com.sadegh.reminder.model.ReminderModel
import java.sql.SQLException


class DBHelper(context: Context?) :
    OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // name of the database file for your application -- change to something appropriate for your app
        private const val DATABASE_NAME = "reminder.db"
        // any time you make changes to your database objects, you may have to increase the database version
        private const val DATABASE_VERSION = 1
        lateinit var instance: DBHelper
    }

    init {
        instance = this
    }

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        try {
            TableUtils.createTable<ReminderModel>(connectionSource, ReminderModel::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }


    }

    override fun onUpgrade(
        database: SQLiteDatabase?,
        connectionSource: ConnectionSource?,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    // the DAO object we use to access the SimpleData table
    private var reminderDao: Dao<ReminderModel, Int>? = null

    fun getReminderDao(): Dao<ReminderModel, Int>? {
        if (reminderDao == null) {
            reminderDao = getDao(ReminderModel::class.java)
        }
        return reminderDao;
    }

    override fun close() {
        super.close()
        reminderDao = null
    }
}