package com.example.machine_learning_calendar

import androidx.compose.runtime.*
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the reference to the CalendarView
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)

        // Set an OnDateChangeListener to the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Display a toast message with the selected date
            // Note: The month value is 0-based in Java and Kotlin, so we add 1 to it
            Toast.makeText(this, "Selected date is $dayOfMonth/${month + 1}/$year", Toast.LENGTH_SHORT).show()
        }
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
