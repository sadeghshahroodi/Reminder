package com.sadegh.reminder.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "reminder")
class ReminderModel {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField
    var name: String = ""
    @DatabaseField
    var description: String = ""
    @DatabaseField
    var time: Long = 0
    @DatabaseField
    var type: Int = 0
    @DatabaseField
    var messageText: String = ""
    @DatabaseField
    var messageReceiver: String = ""
    @DatabaseField
    var repeateAfter: Long = -1


}