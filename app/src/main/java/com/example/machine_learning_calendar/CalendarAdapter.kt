package com.example.machine_learning_calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.view.LayoutInflater

class CalendarAdapter(private val context: Context, private val days: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return days.size
    }

    override fun getItem(position: Int): Any {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView as TextView? ?: LayoutInflater.from(context).inflate(R.layout.day_layout, parent, false) as TextView
        view.text = days[position]
        return view
    }
}
