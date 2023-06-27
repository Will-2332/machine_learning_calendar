package com.example.machine_learning_calendar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

data class Event(val title: String, val start: Long, val end: Long)

class EventsAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val start: TextView = view.findViewById(R.id.start)
        val end: TextView = view.findViewById(R.id.end)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.start.text = Date(event.start).toString() // Convert the start time to a Date and then to a String
        holder.end.text = Date(event.end).toString() // Convert the end time to a Date and then to a String
    }

    override fun getItemCount() = events.size
}

class DayEventsActivity : AppCompatActivity() {

    private val events = mutableListOf<Event>()
    private lateinit var eventsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_events)

        val year = intent.getIntExtra("year", 0)
        val month = intent.getIntExtra("month", 0)
        val day = intent.getIntExtra("day", 0)

        eventsList = findViewById<RecyclerView>(R.id.events_list)
        eventsList.layoutManager = LinearLayoutManager(this)
        eventsList.adapter = EventsAdapter(events)

        fetchEvents(year, month, day) // Fetch the events

        findViewById<Button>(R.id.add_event_button).setOnClickListener {
            showAddEventDialog(year, month, day)
        }
    }

    private fun showAddEventDialog(year: Int, month: Int, day: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()

        val titleInput = dialogView.findViewById<EditText>(R.id.title_input)
        val locationInput = dialogView.findViewById<EditText>(R.id.location_input)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.time_picker)

        dialogView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val title = titleInput.text.toString()
            val location = locationInput.text.toString()
            val begin = Calendar.getInstance().apply {
                set(year, month, day, timePicker.hour, timePicker.minute)
            }
            val end = begin.clone() as Calendar
            end.add(Calendar.HOUR, 1) // For example, set the event's duration to 1 hour

            addEventToCalendar(title, location, begin, end)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun addEventToCalendar(title: String, location: String, begin: Calendar, end: Calendar) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            type = "vnd.android.cursor.item/event"
            putExtra("beginTime", begin.timeInMillis)
            putExtra("allDay", true)
            putExtra("rrule", "FREQ=YEARLY")
            putExtra("endTime", end.timeInMillis + 60 * 60 * 1000)
            putExtra("title", title)
        }
        startActivity(intent)
    }

    private fun fetchEvents(year: Int, month: Int, day: Int) {
        val beginTime = Calendar.getInstance().apply {
            set(year, month, day, 0, 0)
            add(Calendar.DAY_OF_YEAR, -7) // Start from one week before the specified day
        }
        val endTime = beginTime.clone() as Calendar
        endTime.add(Calendar.DAY_OF_YEAR, 15) // End one week after the specified day

        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        val selection = "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTEND} < ?)"
        val selectionArgs = arrayOf(beginTime.timeInMillis.toString(), endTime.timeInMillis.toString())

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 0)
            return
        }

        val cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null)
        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(0)
                val start = it.getLong(1)
                val end = it.getLong(2)

                events.add(Event(title, start, end))
            }
        }
        (eventsList.adapter as EventsAdapter).notifyDataSetChanged()
    }
}
