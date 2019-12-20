package com.sadegh.reminder.io

import com.sadegh.reminder.model.ReminderModel

class DB {
    companion object {

        fun getReminders(): List<ReminderModel> {
            val result = ArrayList<ReminderModel>()
            try {
                result.addAll(DBHelper.instance.getReminderDao()!!.queryForAll())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun getReminder(id: Int): ReminderModel? {
            try {
                return DBHelper.instance.getReminderDao()!!.queryForId(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun addReminder(reminder: ReminderModel): ReminderModel? {
            return DBHelper.instance.getReminderDao()!!.createIfNotExists(reminder)
        }

        fun updateReminder(reminder: ReminderModel): Boolean {
            return DBHelper.instance.getReminderDao()!!.update(reminder) > 0
        }

        fun deleteReminder(reminder: ReminderModel): Boolean {
            return DBHelper.instance.getReminderDao()!!.delete(reminder) > 0
        }

    }
}