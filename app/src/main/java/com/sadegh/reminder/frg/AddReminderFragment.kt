package com.sadegh.reminder.frg

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.sadegh.reminder.R
import com.sadegh.reminder.io.DB
import com.sadegh.reminder.model.ReminderModel
import com.sadegh.reminder.util.ConstantKeys
import com.sadegh.reminder.util.MainConfig
import com.sadegh.reminder.util.Util
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


class AddReminderFragment : BaseFragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    lateinit var etName: EditText
    lateinit var etDesc: EditText
    lateinit var etMsgText: EditText
    lateinit var etMsgReceiver: EditText
    lateinit var repeatContainer: View
    lateinit var etRepeatMinute: EditText
    lateinit var btnTime: Button
    lateinit var btnDate: Button

    var reminderType = 0
    val PICK_CONTACT = 1
    var model = ReminderModel()

    val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_reminder_fragment, container, false)
        reminderType = arguments?.getInt(ConstantKeys.reminderType, 0) ?: 0
        val id = arguments?.getInt(ConstantKeys.id, 0)
        if (id != null) {
            if (id > 0) {
                val tmp = DB.getReminder(id)
                if (tmp != null)
                    model = tmp
            }
        }

        initView(root)

        return root
    }

    private fun initView(root: View) {
        root.findViewById<View>(R.id.btnSave).setOnClickListener(this)
        root.findViewById<View>(R.id.ivChooseContact).setOnClickListener(this)
        btnTime = root.findViewById(R.id.btnTime)
        btnTime.setOnClickListener(this)
        btnDate = root.findViewById(R.id.btnDate)
        btnDate.setOnClickListener(this)
        val btnCancel: Button = root.findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener(this)
        etName = root.findViewById(R.id.etName)
        etDesc = root.findViewById(R.id.etDescription)
        etMsgText = root.findViewById(R.id.etMessageText)
        etMsgReceiver = root.findViewById(R.id.etMessageReceiver)
        val msgInfoContiner = root.findViewById<View>(R.id.msgInfoContainer)
        val switchRepeat = root.findViewById<SwitchCompat>(R.id.swRepeat)
        switchRepeat.setOnCheckedChangeListener(this)
        repeatContainer = root.findViewById(R.id.repeatContainer)
        etRepeatMinute = root.findViewById(R.id.repeatMinute)

        if (model.id > 0) {
            btnCancel.setText(R.string.remove)
            etName.setText(model.name)
            etDesc.setText(model.description)
            etMsgText.setText(model.messageText)
            etMsgReceiver.setText(model.messageReceiver)
            if (model.repeateAfter > 0) {
                etRepeatMinute.setText((model.repeateAfter / 60f / 60000).toString())
                switchRepeat.isChecked = true
            }
            reminderType = model.type

            calendar.timeInMillis = model.time
            setTextForDateAndTime(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            setTextForDateAndTime(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
        }


        if (reminderType == 1) {
            msgInfoContiner.visibility = View.VISIBLE
        } else {
            msgInfoContiner.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                model.name = etName.text.toString()
                if (TextUtils.isEmpty(model.name)) {
                    etName.error = getString(R.string.nameCantBeEmpty)
                    etName.requestFocus()
                    return
                }
                val cal = Calendar.getInstance()

                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                if (calendar.before(cal)) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                model.time = calendar.timeInMillis

                model.description = etDesc.text.toString()

                model.type = reminderType
                model.messageText = etMsgText.text.toString()
                model.messageReceiver = etMsgReceiver.text.toString()

                if (reminderType == 1) {
                    if (TextUtils.isEmpty(model.messageText)) {
                        etMsgText.error = getString(R.string.messageTextCantBeEmpty)
                        etMsgText.requestFocus()
                        return
                    }
                    if (TextUtils.isEmpty(model.messageReceiver)) {
                        etMsgReceiver.error =getString(R.string.receiverNumberCantBeEmpty)
                        etMsgReceiver.requestFocus()
                        return
                    }
                }
                var repeat: Float = -1f
                if (repeatContainer.visibility == View.VISIBLE) {
                    try {
                        repeat = etRepeatMinute.text.toString().toFloat()
                    } catch (ignored: java.lang.Exception) {
                    }
                }
                model.repeateAfter = (repeat * 60 * 60000L).toLong()

                if (model.id < 1) {
                    model = DB.addReminder(model)!!
                    context?.let { Util.addReminderBroadcast(it, model) }
                } else {
                    DB.updateReminder(model)
                    context?.let {
                        Util.cancelReminderBroadcast(it, model)
                        Util.addReminderBroadcast(it, model)
                    }

                }



                activity?.onBackPressed()
            }

            R.id.btnTime -> {
                val dialog = context?.let {
                    var hour = 8
                    var min = 0
                    if (model.id > 0) {
                        hour = calendar.get(Calendar.HOUR_OF_DAY)
                        min = calendar.get(Calendar.MINUTE)
                    }
                    TimePickerDialog(it, this, hour, min, true)
                }
                dialog?.let {
                    dialog.show()
                }
            }

            R.id.btnDate -> {
                val dialog = context?.let {
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(it, this, year, month, day)

                }
                dialog?.let {
                    dialog.show()
                }
            }

            R.id.btnCancel -> {
                if (model.id > 0) {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setMessage(
                                String.format(
                                    getString(R.string.removeReminder),
                                    model.name
                                )
                            )
                            .setPositiveButton(R.string.yes) { _, _ ->
                                DB.deleteReminder(model)
                                context?.let { it -> Util.cancelReminderBroadcast(it, model) }
                                activity?.onBackPressed()
                            }.setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.cancel()
                            }.show()
                    }
                } else {
                    activity?.onBackPressed()
                }
            }

            R.id.ivChooseContact -> {

                val intent =
                    Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, PICK_CONTACT)
            }
        }
    }


    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        when (reqCode) {
            PICK_CONTACT -> if (resultCode == Activity.RESULT_OK) {

                try {
                    val contactData = data?.data
                    val c = context?.contentResolver?.query(
                        contactData, null, null, null, null
                    )
                    if (c != null && c.moveToFirst()) {


                        val id =
                            c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                        val hasPhone =
                            c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                        if (hasPhone == "1") {
                            val phones = context?.contentResolver?.query(
                                Phone.CONTENT_URI, null,
                                Phone.CONTACT_ID + " = " + id,
                                null, null
                            )
                            phones?.moveToFirst()
                            if (phones != null) {
                                val list = ArrayList<String>()
                                do {
                                    val cNumber =
                                        phones.getString(phones.getColumnIndex("data1")).trim()
                                            .replace(" ", "")
                                    if (!list.contains(cNumber)) {
                                        list.add(cNumber)
                                        println("number is:$cNumber")
                                    }
                                } while (phones.moveToNext())
                                if (list.isNotEmpty()) {
                                    if (list.size == 1) {
                                        etMsgReceiver.setText(list[0])
                                    } else {
                                        chooseANumberFromList(list)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun chooseANumberFromList(list: ArrayList<String>) {
        if (activity == null || activity!!.isFinishing)
            return
        val builderSingle =
            AlertDialog.Builder(activity!!)
//        builderSingle.setIcon(R.drawable.ic)
        builderSingle.setTitle(R.string.pleaseChooseANumber)

        val arrayAdapter = ArrayAdapter<String>(
            activity,
            android.R.layout.select_dialog_singlechoice
        )
        for (num in list)
            arrayAdapter.add(num)

        builderSingle.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog, _ -> dialog.dismiss() }

        builderSingle.setAdapter(
            arrayAdapter
        ) { _, which ->
            val strName = arrayAdapter.getItem(which)
            etMsgReceiver.setText(strName)
        }
        builderSingle.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            repeatContainer.visibility = View.VISIBLE
        } else {
            repeatContainer.visibility = View.GONE
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        setTextForDateAndTime(0, 0, 0, hourOfDay, minute, false)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        setTextForDateAndTime(year, month, dayOfMonth, 0, 0, true)
    }

    private fun setTextForDateAndTime(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int,
        isDate: Boolean
    ) {
        if (isDate) {
            val timeString = getString(R.string.date) +
                    " (" +
                    String.format(Locale.US, "%04d", year) +
                    "/" +
                    String.format(Locale.US, "%02d", (month + 1)) +
                    "/" +
                    String.format(Locale.US, "%02d", dayOfMonth) +
                    ")"
            btnDate.text = timeString
        } else {
            val timeString = getString(R.string.time) +
                    " (" +
                    String.format(Locale.US, "%02d", hourOfDay) +
                    ":" +
                    String.format(Locale.US, "%02d", minute) +
                    ")"
            btnTime.text = timeString
        }
    }
}
