package com.example.machine_learning_calendar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

class DayEventsActivity : AppCompatActivity() {

    private lateinit var dbHelper: EventsDatabaseHelper
    private var year = 0
    private var month = 0
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_events)

        dbHelper = EventsDatabaseHelper(this)

        // Get the date information from the intent extras
        year = intent.getIntExtra("year", 0)
        month = intent.getIntExtra("month", 0)
        day = intent.getIntExtra("day", 0)

        val addEventButton = findViewById<Button>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("year", year)
            intent.putExtra("month", month)
            intent.putExtra("day", day)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()

        // Use the date information to display events for the selected day
        val events = dbHelper.getEventsForDay(year, month, day)
        Log.d("DayEventsActivity", "Getting events for day: $year-$month-$day")

        // Get the reference to the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.events_list)

        // Set the layout manager and adapter for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventsAdapter(events)
    }


    private fun createEvent(title: String, location: String, startTime: String, endTime: String) {
        val date = "$year-${month+1}-$day" // Construct the date string in the format "YYYY-MM-DD"
        val event = Event(0, date, title, location, startTime, endTime, "", 0)
        dbHelper.createEvent(event)
    }

// Define the EventsAdapter class here
class EventsAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val location: TextView = view.findViewById(R.id.location)
        val startTime: TextView = view.findViewById(R.id.start_time)
        val endTime: TextView = view.findViewById(R.id.end_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.location.text = event.location
        holder.startTime.text = event.startTime
        holder.endTime.text = event.endTime
    }

    override fun getItemCount() = events.size
}
}