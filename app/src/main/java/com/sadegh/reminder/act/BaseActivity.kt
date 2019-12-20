package com.sadegh.reminder.act

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sadegh.reminder.R

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    abstract fun pageTitle(): String

    override fun onStart() {
        super.onStart()
        addToolbar()
    }

    fun addToolbar() {
        val container = findViewById<LinearLayout>(R.id.toolbarContainer)
        if (container != null) {
            val toolbar =
                LayoutInflater.from(this).inflate(R.layout.common_toolbar, container, false)
            container.addView(toolbar)
            toolbar.findViewById<View>(R.id.toolabrBackBtn).setOnClickListener(this)
            toolbar.findViewById<TextView>(R.id.toolbarText).text = pageTitle()
        }
    }

    override fun onClick(v: View?) {
        if (v == null)
            return
        when (v.id) {
            R.id.toolabrBackBtn -> {
                onBackPressed()
            }

        }
    }


}