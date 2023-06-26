package com.example.machine_learning_calendar

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DayEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_events)

        val year = intent.getIntExtra("year", 0)
        val month = intent.getIntExtra("month", 0)
        val day = intent.getIntExtra("day", 0)

        // TODO: Fetch and display the events for this day

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

            addEvent(title, location, begin, end)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun addEvent(title: String, location: String, begin: Calendar, end: Calendar) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.timeInMillis)
            .putExtra(CalendarContract.Events.TITLE, title)
            .putExtra(CalendarContract.Events.DESCRIPTION, "Event Description")
            .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent)
}
}
