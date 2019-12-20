package com.sadegh.reminder.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sadegh.reminder.R
import com.sadegh.reminder.adapter.vh.ReminderViewHolder
import com.sadegh.reminder.model.IOnItemClickListener
import com.sadegh.reminder.model.ReminderModel
import com.sadegh.reminder.util.Util
import java.util.*
import kotlin.collections.ArrayList

class ReminderAdapter(context: Context) : RecyclerView.Adapter<ReminderViewHolder>() {
    var items: List<ReminderModel> = ArrayList()
    var iOnItemClickListener: IOnItemClickListener? = null
    var enableColor: Int = 0
    var disableColor: Int = 0
    var enabledrawable: Drawable
    var disabledrawable: Drawable

    init {
        enableColor = context.getColor(R.color.black)
        disableColor = context.getColor(R.color.gray)
        enabledrawable = context.getDrawable(R.drawable.reminder_item_background)
        disabledrawable = context.getDrawable(R.drawable.reminder_item_background_dis)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view, iOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val item = items[position]
        val enable = item.time > System.currentTimeMillis() || item.repeateAfter > 0

        holder.name.text = item.name
        holder.description.text = item.description
        val cal = Calendar.getInstance()
        cal.timeInMillis = item.time
        holder.addTime.text = getOrdinalDateTime(cal)

        if (enable) {
            cal.timeInMillis = Util.fixReminderTime(item)
            holder.nextTime.text = getOrdinalDateTime(cal)
        } else {
            holder.nextTime.setText(R.string.expired)
        }

        if (enable) {
            holder.itemView.background = enabledrawable
            holder.name.setTextColor(enableColor)
            holder.description.setTextColor(enableColor)
            holder.addTime.setTextColor(enableColor)
            holder.nextTime.setTextColor(enableColor)
        } else {
            holder.itemView.background = disabledrawable
            holder.name.setTextColor(disableColor)
            holder.description.setTextColor(disableColor)
            holder.addTime.setTextColor(disableColor)
            holder.nextTime.setTextColor(disableColor)
        }
    }

    private fun getOrdinalDateTime(calendar: Calendar): String {
        val today = Calendar.getInstance()
        val showDate: Boolean =
            calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR) ||
                    calendar.get(Calendar.MONTH) != today.get(Calendar.MONTH) ||
                    calendar.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)
        val builder = StringBuilder()
        if (showDate) {
            builder.append(String.format(Locale.US, "%04d", calendar.get(Calendar.YEAR)))
                .append("/")
                .append(String.format(Locale.US, "%02d", calendar.get(Calendar.MONTH) + 1))
                .append("/")
                .append(String.format(Locale.US, "%02d", calendar.get(Calendar.DAY_OF_MONTH)))
                .append(" ")
        }
        builder.append(String.format(Locale.US, "%02d", calendar.get(Calendar.HOUR_OF_DAY)))
            .append(":")
            .append(String.format(Locale.US, "%02d", calendar.get(Calendar.MINUTE)))
        return builder.toString()
    }

}