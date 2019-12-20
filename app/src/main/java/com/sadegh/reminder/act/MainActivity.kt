package com.sadegh.reminder.act

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sadegh.reminder.R
import com.sadegh.reminder.frg.AddReminderFragment
import com.sadegh.reminder.frg.ReminderListFragment
import com.sadegh.reminder.util.ConstantKeys
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil


class MainActivity : BaseActivity(),
    RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any> {
    lateinit var rfabHelper: RapidFloatingActionHelper
    lateinit var reminderList: ReminderListFragment
    override fun pageTitle(): String {
        return getString(R.string.homePage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        val permissions = arrayOf(
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CONTACTS
        )
        requestPermissions(permissions, 1)
    }

    override fun onStart() {
        super.onStart()
        var isAddReminderUp = false
        for (frg in supportFragmentManager.fragments) {
            if (frg is AddReminderFragment) {
                isAddReminderUp = true
                break
            }
        }
        controlToolbar(isAddReminderUp)
    }

    private fun initView() {
        reminderList = ReminderListFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frgContainer, reminderList, ConstantKeys.reminderList)
            .commitAllowingStateLoss()

        //region fab btn
        val rfaContent = RapidFloatingActionContentLabelList(this)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
        val items: MutableList<RFACLabelItem<*>> = ArrayList()


        items.add(
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.reminder))
                .setResId(R.mipmap.alarm)
                .setIconNormalColor(resources.getColor(R.color.colorPrimary))
                .setIconPressedColor(resources.getColor(R.color.colorPrimary))
                .setWrapper(0)
        )
        items.add(
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.sendSms))
                .setResId(R.mipmap.message)
                .setIconNormalColor(resources.getColor(R.color.colorPrimary))
                .setIconPressedColor(resources.getColor(R.color.colorPrimary))
                .setWrapper(1)
        )
        rfaContent
            .setItems(items)
            .setIconShadowRadius(RFABTextUtil.dip2px(this, 5f))
            .setIconShadowColor(-0x777778)
            .setIconShadowDy(RFABTextUtil.dip2px(this, 5f))

        rfabHelper = RapidFloatingActionHelper(
            this,
            findViewById(R.id.activity_main_rfal),
            findViewById(R.id.addReminder),
            rfaContent
        ).build()
        //endregion

    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<Any>?) {
        onRFACItemIconClick(position, item)

    }

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Any>?) {
        rfabHelper.toggleContent()

        goToAddReminderFragment(position, 0)

    }

    private fun controlToolbar(isAddReminderFrgAdded: Boolean) {
        val back = findViewById<View>(R.id.toolabrBackBtn)
        val title: TextView = findViewById(R.id.toolbarText)
        if (isAddReminderFrgAdded) {
            back.visibility = View.VISIBLE
            title.setText(R.string.addReminder)
        } else {
            back.visibility = View.GONE
            title.setText(R.string.remindersList)
        }
    }

    /**
     * @param reminderType : 0 = normal reminder, 1 = sms reminder
     */
    fun goToAddReminderFragment(reminderType: Int, reminderId: Int) {
        val frg = AddReminderFragment()

        val bundle = Bundle()
        bundle.putInt(ConstantKeys.reminderType, reminderType)
        bundle.putInt(ConstantKeys.id, reminderId)
        frg.arguments = bundle
        supportFragmentManager.beginTransaction()
            .add(R.id.frgContainer, frg, ConstantKeys.addReminder)
            .commitAllowingStateLoss()
        findViewById<View>(R.id.activity_main_rfal).visibility = View.GONE

        controlToolbar(true)
    }

    override fun onBackPressed() {
        val frg = supportFragmentManager.findFragmentByTag(ConstantKeys.addReminder)
        if (frg != null) {
            supportFragmentManager.beginTransaction().remove(frg).commitAllowingStateLoss()
            findViewById<View>(R.id.activity_main_rfal).visibility = View.VISIBLE
            reminderList.fillViewByReminders()
            controlToolbar(false)
            return
        }
        super.onBackPressed()

    }

}