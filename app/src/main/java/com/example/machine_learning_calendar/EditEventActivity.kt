package com.example.machine_learning_calendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.TimePicker
import android.util.Log

class EditEventActivity : AppCompatActivity() {
    private lateinit var dbHelper: EventsDatabaseHelper
    private lateinit var event: Event
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        dbHelper = EventsDatabaseHelper(this)

        // Get the eventId from the intent extras and retrieve the event from the database
        val eventId = intent.getIntExtra("eventId", 0)
        event = dbHelper.getEventById(eventId) ?: return finish() // Close the activity if the event is null
        title = "Editing " + event.title
        Log.d("EditEventActivity", "Editing event: $event")

        val titleInput = findViewById<EditText>(R.id.title_edit_text)
        val locationInput = findViewById<EditText>(R.id.location_edit_text)

        // Populate the input fields with the details of the event
        titleInput.setText(event.title)
        locationInput.setText(event.location)

        startTimePicker = findViewById(R.id.start_time_picker)
        endTimePicker = findViewById(R.id.end_time_picker)

        // Populate the time pickers with the start and end times of the event
        val (startHour, startMinute) = event.startTime.split(":").map { it.toInt() }
        startTimePicker.hour = startHour
        startTimePicker.minute = startMinute

        val (endHour, endMinute) = event.endTime.split(":").map { it.toInt() }
        endTimePicker.hour = endHour
        endTimePicker.minute = endMinute

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val location = locationInput.text.toString()

            val startHour = startTimePicker.hour
            val startMinute = startTimePicker.minute
            val startTime = "$startHour:$startMinute"

            val endHour = endTimePicker.hour
            val endMinute = endTimePicker.minute
            val endTime = "$endHour:$endMinute"

            // Create a new Event object with the updated properties
            val updatedEvent = Event(event.id, event.date, title, location, startTime, endTime, event.suggestion, event.grade)
            updateEvent(updatedEvent)

            // Close the activity and go back to the previous screen
            finish()
        }

        val deleteButton = findViewById<Button>(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteEvent()

            // Close the activity and go back to the previous screen
            finish()
        }
    }

    private fun updateEvent(updatedEvent: Event) {
        // Update the event in the database
        Log.d("EditEventActivity", "Updating event: $updatedEvent")
        dbHelper.updateEvent(updatedEvent)
    }

    private fun deleteEvent() {
        // Delete the event from the database
        Log.d("EditEventActivity", "Deleting event: $event")
        dbHelper.deleteEvent(event)
    }
}
