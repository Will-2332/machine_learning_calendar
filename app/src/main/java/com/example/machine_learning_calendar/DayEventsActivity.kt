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
import android.widget.RatingBar

class DayEventsActivity : AppCompatActivity() {

    private lateinit var dbHelper: EventsDatabaseHelper
    private var year = 0
    private var month = 0
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_events)
        Log.d("DayEventsActivity", "onCreate called.")

        dbHelper = EventsDatabaseHelper(this)

        // Get the date information from the intent extras
        year = intent.getIntExtra("year", 0)
        month = intent.getIntExtra("month", 0)
        day = intent.getIntExtra("day", 0)

        // Set the title of the activity to the selected date
        val date = "$year-${month+1}-$day"
        title = date

        val addEventButton = findViewById<Button>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("year", year)
            intent.putExtra("month", month)
            intent.putExtra("day", day)
            Log.d("DayEventsActivity", "Creating an event at : $year-$month-$day")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DayEventsActivity", "onResume called.")

        // Use the date information to display events for the selected day
        val events = dbHelper.getEventsForDay(year, month, day)
        Log.d("DayEventsActivity", "Getting events for day: $year-$month-$day")

        // Get the reference to the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.events_list)

        // Set the layout manager and adapter for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventsAdapter(events, dbHelper)
        Log.d("DayEventsActivity", "RecyclerView adapter set.")
    }

    // Define the EventsAdapter class here
    class EventsAdapter(private val events: List<Event>, private val dbHelper: EventsDatabaseHelper) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.title)
            val location: TextView = view.findViewById(R.id.location)
            val startTime: TextView = view.findViewById(R.id.start_time)
            val endTime: TextView = view.findViewById(R.id.end_time)
            val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)  // New UI element for grading
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val event = events[position]
            Log.d("EventsAdapter", "Binding event: ${event.title}")

            holder.title.text = event.title
            holder.location.text = event.location
            holder.startTime.text = event.startTime
            holder.endTime.text = event.endTime

            if (event.suggestion == "1") {
                holder.ratingBar.visibility = View.VISIBLE
                holder.ratingBar.rating = event.grade.toFloat()

                holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    val newGrade = rating.toInt()
                    event.grade = newGrade
                    dbHelper.updateEventGrade(event.id, newGrade)
                    Log.d("EventsAdapter", "Updated grade for event: ${event.title} to $newGrade")
                }
            } else {
                holder.ratingBar.visibility = View.GONE
            }

            // Keep your existing click listener
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditEventActivity::class.java)
                intent.putExtra("eventId", event.id)
                Log.d("EventsAdapter", "Clicked on event: ${event.title}, launching EditEventActivity.")
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount() = events.size
    }
}