package com.sadegh.reminder.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Contacts.GroupMembership.GROUP_ID
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sadegh.reminder.R
import com.sadegh.reminder.model.ReminderModel
import kotlin.random.Random

class NotificationController {
    companion object {
        private const val GROUP_KEY = "GroupReminder"
        private const val CHANNEL_NAME = "Reminder"
        private const val SCHANNEL_NAME = "SReminder"
        private const val CHANNEL_ID = "ReminderID"
        private const val SCHANNEL_ID = "SReminderID"


        fun showTextNotif(context: Context, reminder: ReminderModel) {

            try {

                val nm =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                var summaryNotification: Notification? = null

                if (Build.VERSION.SDK_INT >= 24) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                            val channel = NotificationChannel(
                                CHANNEL_ID, CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            channel.description = CHANNEL_NAME
                            nm.createNotificationChannel(channel)
                        }
                        if (nm.getNotificationChannel(SCHANNEL_ID) == null) {
                            val silentChannel = NotificationChannel(
                                SCHANNEL_ID, SCHANNEL_NAME,
                                NotificationManager.IMPORTANCE_NONE
                            )
                            silentChannel.description = SCHANNEL_NAME
                            nm.createNotificationChannel(silentChannel)
                        }
                    }
                    summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher_notif)
                        .setStyle(
                            NotificationCompat.InboxStyle()
                                .setBigContentTitle(reminder.description)
                                .setSummaryText(reminder.name)
                        )
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                        .build()
                }


                val builder = NotificationCompat.Builder(context, CHANNEL_ID)


                builder.color = context.resources.getColor(R.color.colorPrimaryDark)

                builder.setDefaults(Notification.DEFAULT_LIGHTS)

                builder.setGroup(GROUP_KEY)
                builder.setChannelId(CHANNEL_ID)
                builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                builder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(reminder.name)
                val style =
                    NotificationCompat.BigTextStyle()
                        .bigText(reminder.description)
                        .setBigContentTitle(reminder.name)
                        .setSummaryText(reminder.name)
                builder.setStyle(style)
                builder.priority = NotificationCompat.PRIORITY_MAX
                builder.setAutoCancel(true)


                val notificationIntent = Intent(Intent.ACTION_VIEW)

                builder.setSmallIcon(R.mipmap.ic_launcher_notif)

                val resultPendingIntent = PendingIntent.getActivity(
                    context,
                    reminder.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                builder.setContentIntent(resultPendingIntent)
                val notification = builder.build()

                NotificationManagerCompat.from(context).apply {
                    notify(reminder.id, notification)
                    summaryNotification?.let { notify(1, it) }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}