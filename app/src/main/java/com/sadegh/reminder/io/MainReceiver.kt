package com.sadegh.reminder.io

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sadegh.reminder.util.ConstantKeys
import com.sadegh.reminder.util.NotificationController
import com.sadegh.reminder.util.Util

class MainReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null)
            return
        if (intent != null) {
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                val reminders = DB.getReminders()
                for (reminder in reminders) {
                    if (reminder.repeateAfter > 0 || reminder.time > System.currentTimeMillis()) {
                        Util.addReminderBroadcast(context, reminder)
                    }
                }
            } else {
                val id = intent.getIntExtra(ConstantKeys.id, 0)

                if (id > 0) {
                    DB.getReminder(id)?.let { reminder ->
                        if (reminder.type == 0) {
                            NotificationController.showTextNotif(
                                context, reminder
                            )
                        } else if (reminder.type == 1) {
                            Util.sendSms(reminder)
                        }
                    }
                }
            }
        }
    }
}