package com.sadegh.reminder.frg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sadegh.reminder.R
import com.sadegh.reminder.act.MainActivity
import com.sadegh.reminder.adapter.ReminderAdapter
import com.sadegh.reminder.io.DB
import com.sadegh.reminder.model.IOnItemClickListener
import com.sadegh.reminder.model.ReminderModel
import com.sadegh.reminder.model.SimpleSubscriber
import com.sadegh.reminder.util.MainConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ReminderListFragment : BaseFragment(), View.OnClickListener, IOnItemClickListener {
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root= inflater.inflate(R.layout.reminder_list_frg, container, false)
        initView(root)

        return root
    }
    private fun initView(root: View) {
        val list = root.findViewById<RecyclerView>(R.id.mainList)
        list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        context?.let {
            adapter = ReminderAdapter(it)
            adapter.iOnItemClickListener = this
            list.adapter = adapter
        }

        if (MainConfig.isForNgr) {
            val drawableResourceId = resources
                .getIdentifier("ngr", "mipmap", context?.packageName)
            root.findViewById<ImageView>(R.id.backgroundImage).setBackgroundResource(drawableResourceId)
        }
    }

    override fun onResume() {
        super.onResume()
        fillViewByReminders()
    }

    fun fillViewByReminders() {
        Observable.just(DB.getReminders())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SimpleSubscriber<List<ReminderModel>>() {
                override fun onNext(t: List<ReminderModel>) {
                    adapter.items = t
                    adapter.notifyDataSetChanged()
                }
            })


    }

    override fun onClick(v: View?) {
        if (v == null)
            return
    }

    override fun onItemClick(view: View?, position: Int) {

        activity?.let{
            if (activity is MainActivity) {
                (activity as MainActivity).goToAddReminderFragment(0,adapter.items[position].id)
            }
        }}

}