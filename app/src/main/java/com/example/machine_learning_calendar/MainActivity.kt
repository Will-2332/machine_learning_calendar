package com.example.machine_learning_calendar

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the reference to the CalendarView
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)

        // Set an OnDateChangeListener to the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDay(year, month, dayOfMonth)
        }
    }

    private fun selectDay(year: Int, month: Int, dayOfMonth: Int) {
        val intent = Intent(this, DayEventsActivity::class.java).apply {
            putExtra("year", year)
            putExtra("month", month)
            putExtra("day", dayOfMonth)
        }
        startActivity(intent)
    }
}
