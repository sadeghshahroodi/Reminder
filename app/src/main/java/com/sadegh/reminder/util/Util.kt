package com.sadegh.reminder.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.sadegh.reminder.io.DB
import com.sadegh.reminder.io.MainReceiver
import com.sadegh.reminder.model.ReminderModel
import java.util.*


class Util {
    companion object {

        fun addReminderBroadcast(ctx: Context, reminderModel: ReminderModel) {
            try {
//                if (true)return
                Log.d("addBroadcast", reminderModel.id.toString())
                if (isReminderRegistered(ctx, reminderModel))
                    return

                val am =
                    ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val serviceIntent = Intent(ctx, MainReceiver::class.java)
                serviceIntent.action = "WAKEUP"
                serviceIntent.putExtra(ConstantKeys.id, reminderModel.id)
                val servicePendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    reminderModel.id,  // integer constant used to identify the service
                    serviceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ) // FLAG to avoid creating a second service if there's already one running

                reminderModel.time = fixReminderTime(reminderModel)

                if (reminderModel.repeateAfter > 0) {
                    am.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        reminderModel.time,
                        reminderModel.repeateAfter,
                        servicePendingIntent
                    )
                } else
                    am.set(AlarmManager.RTC_WAKEUP, reminderModel.time, servicePendingIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun cancelReminderBroadcast(ctx: Context, reminderModel: ReminderModel) {
            try {
                Log.d("addBroadcast", reminderModel.id.toString())

                val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val serviceIntent = Intent(ctx, MainReceiver::class.java)
                serviceIntent.action = "WAKEUP"
                serviceIntent.putExtra(ConstantKeys.id, reminderModel.id)
                val servicePendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    reminderModel.id,  // integer constant used to identify the service
                    serviceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ) // FLAG to avoid creating a second service if there's already one running
                am.cancel(servicePendingIntent)
                servicePendingIntent.cancel()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun checkAlarmManger(ctx: Context) {
            val reminders = DB.getReminders()
            for (reminder in reminders) {
                if (reminder.repeateAfter > 0 || reminder.time > System.currentTimeMillis()) {
                    addReminderBroadcast(ctx, reminder)
                }
            }
        }

        private fun isReminderRegistered(context: Context, reminderModel: ReminderModel): Boolean {
            return PendingIntent.getBroadcast(
                context,
                reminderModel.id,
                Intent(context, MainReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            ) != null
        }

        fun sendSms(reminder: ReminderModel) {
            Log.d("sendsms", "r = " + reminder.id)
            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    reminder.messageReceiver,
                    null,
                    reminder.messageText,
                    null,
                    null
                )

            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }


        fun fixReminderTime(reminderModel: ReminderModel): Long {
            val currentTime = System.currentTimeMillis()
            if (reminderModel.time > currentTime)
                return reminderModel.time
            val diff = currentTime - reminderModel.time
            val tmp = diff / reminderModel.repeateAfter
            return reminderModel.time + ((tmp + 1) * reminderModel.repeateAfter)

        }
    }
}