package com.sadegh.reminder.model

import android.view.View

interface IOnItemClickListener {
    fun onItemClick(view: View?, position: Int)
}