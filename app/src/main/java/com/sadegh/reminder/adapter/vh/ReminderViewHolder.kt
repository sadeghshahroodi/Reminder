package com.sadegh.reminder.adapter.vh

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadegh.reminder.R
import com.sadegh.reminder.model.IOnItemClickListener

class ReminderViewHolder(itemView: View,onItemClickListener:IOnItemClickListener?) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    private val iOnItemClickListener:IOnItemClickListener? = onItemClickListener
    val name: TextView = itemView.findViewById(R.id.name)
    val description: TextView = itemView.findViewById(R.id.description)
    val addTime: TextView = itemView.findViewById(R.id.addTime)
    val nextTime: TextView = itemView.findViewById(R.id.nextTime)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        iOnItemClickListener?.onItemClick(v,adapterPosition)
    }
}