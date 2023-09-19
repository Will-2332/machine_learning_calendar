package com.example.machine_learning_calendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.TimePicker
import android.util.Log

class AddEventActivity : AppCompatActivity() {
    private lateinit var dbHelper: EventsDatabaseHelper
    private var year = 0
    private var month = 0
    private var day = 0
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        title = "Add Event"

        Log.d("AddEventActivity", "Activity started")

        dbHelper = EventsDatabaseHelper(this)

        // Get the date information from the intent extras
        year = intent.getIntExtra("year", 0)
        month = intent.getIntExtra("month", 0)
        day = intent.getIntExtra("day", 0)

        Log.d("AddEventActivity", "Received date: $year-$month-$day")

        val titleInput = findViewById<EditText>(R.id.title_edit_text)
        val locationInput = findViewById<EditText>(R.id.location_edit_text)

        startTimePicker = findViewById(R.id.start_time_picker)
        endTimePicker = findViewById(R.id.end_time_picker)

        val okButton = findViewById<Button>(R.id.ok_button)
        okButton.setOnClickListener {
            val title = titleInput.text.toString()
            val location = locationInput.text.toString()

            val startHour = startTimePicker.hour
            val startMinute = startTimePicker.minute
            val startTime = "$startHour:$startMinute"

            val endHour = endTimePicker.hour
            val endMinute = endTimePicker.minute
            val endTime = "$endHour:$endMinute"

            val endNextDay = endHour < startHour || (endHour == startHour && endMinute <= startMinute)

            Log.d("AddEventActivity", "Collected event details: Title=$title, Location=$location, StartTime=$startTime, EndTime=$endTime, EndNextDay=$endNextDay")

            createEvent(title, location, startTime, endTime, endNextDay)

            // Close the activity and go back to the previous screen
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            Log.d("AddEventActivity", "Cancel button clicked")
            // Close the activity and go back to the previous screen without saving anything
            finish()
        }
    }

    private fun createEvent(title: String, location: String, startTime: String, endTime: String, endNextDay: Boolean) {
        val date = "$year-${month+1}-$day" // Construct the date string in the format "YYYY-MM-DD"
        val event = Event(0, date, title, location, startTime, endTime, "0", 0, endNextDay) // Assuming Event class has endNextDay field
        Log.d("AddEventActivity", "Creating event: $event")
        dbHelper.createEvent(event)
    }
}
