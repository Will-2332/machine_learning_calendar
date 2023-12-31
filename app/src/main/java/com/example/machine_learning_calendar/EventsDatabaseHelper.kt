package com.example.machine_learning_calendar

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate

data class Event(val id: Int, val date: String, val title: String, val location: String, val startTime: String, val endTime: String, val suggestion: String, var grade: Int, var endNextDay: Boolean) {
    companion object {
        const val TABLE_NAME = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_TITLE = "title"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"
        const val COLUMN_SUGGESTION = "suggestion"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_END_NEXT_DAY = "end_next_day"
    }
}

class EventsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "events.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EVENTS_TABLE = "CREATE TABLE ${Event.TABLE_NAME} (" +
                "${Event.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${Event.COLUMN_DATE} DATE," +
                "${Event.COLUMN_TITLE} TEXT," +
                "${Event.COLUMN_LOCATION} TEXT," +
                "${Event.COLUMN_START_TIME} TIME," +
                "${Event.COLUMN_END_TIME} TIME," +
                "${Event.COLUMN_SUGGESTION} BINARY," +
                "${Event.COLUMN_GRADE} INTEGER,"+
                "${Event.COLUMN_END_NEXT_DAY} INTEGER)"
        db.execSQL(CREATE_EVENTS_TABLE)
        Log.d("EventsDatabaseHelper", "Creating database : ${Event.TABLE_NAME}")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Event.TABLE_NAME}")
        onCreate(db)
        Log.d("EventsDatabaseHelper", "Database upgraded from version $oldVersion to $newVersion")
    }

    fun getEventsForDay(year: Int, month: Int, day: Int): List<Event> {
        val date = "$year-${month+1}-$day" // Construct the date string in the format "YYYY-MM-DD"
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${Event.TABLE_NAME} WHERE ${Event.COLUMN_DATE} = ?", arrayOf(date))
        Log.d("EventsDatabaseHelper", "Getting events for day: $year-$month-$day")
        val events = mutableListOf<Event>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(Event.COLUMN_ID))
                val title = it.getString(it.getColumnIndex(Event.COLUMN_TITLE))
                val location = it.getString(it.getColumnIndex(Event.COLUMN_LOCATION))
                val startTime = it.getString(it.getColumnIndex(Event.COLUMN_START_TIME))
                val endTime = it.getString(it.getColumnIndex(Event.COLUMN_END_TIME))
                val suggestion = it.getString(it.getColumnIndex(Event.COLUMN_SUGGESTION))
                val grade = it.getInt(it.getColumnIndex(Event.COLUMN_GRADE))
                val endNextDay = it.getInt(it.getColumnIndex(Event.COLUMN_END_NEXT_DAY)) == 1
                events.add(Event(id, date, title, location, startTime, endTime, suggestion, grade, endNextDay))
            }
        }
        Log.d("EventsDatabaseHelper", "Fetched ${events.size} events for the day")
        return events
    }

    fun createEvent(event: Event) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Event.COLUMN_DATE, event.date)
            put(Event.COLUMN_TITLE, event.title)
            put(Event.COLUMN_LOCATION, event.location)
            put(Event.COLUMN_START_TIME, event.startTime)
            put(Event.COLUMN_END_TIME, event.endTime)
            put(Event.COLUMN_SUGGESTION, event.suggestion)
            put(Event.COLUMN_GRADE, event.grade)
            put(Event.COLUMN_END_NEXT_DAY, if (event.endNextDay) 1 else 0)
        }
        Log.d("EventsDatabaseHelper", "Creating event: $event") // Add this line
        db.insert(Event.TABLE_NAME, null, values)
        Log.d("EventsDatabaseHelper", "Event created successfully")
    }

    fun updateEvent(event: Event) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Event.COLUMN_DATE, event.date)
            put(Event.COLUMN_TITLE, event.title)
            put(Event.COLUMN_LOCATION, event.location)
            put(Event.COLUMN_START_TIME, event.startTime)
            put(Event.COLUMN_END_TIME, event.endTime)
            put(Event.COLUMN_SUGGESTION, event.suggestion)
            put(Event.COLUMN_GRADE, event.grade)
            put(Event.COLUMN_END_NEXT_DAY, if (event.endNextDay) 1 else 0)
        }
        Log.d("EventsDatabaseHelper", "Updating event: $event") // Add this line
        db.update(Event.TABLE_NAME, values, "${Event.COLUMN_ID} = ?", arrayOf(event.id.toString()))
        Log.d("EventsDatabaseHelper", "Event updated successfully")

    }

    fun deleteEvent(event: Event) {
        val db = this.writableDatabase
        Log.d("EventsDatabaseHelper", "Deleting event: $event") // Add this line
        db.delete(Event.TABLE_NAME, "${Event.COLUMN_ID} = ?", arrayOf(event.id.toString()))
        Log.d("EventsDatabaseHelper", "Event deleted successfully")
    }

    fun getEventById(id: Int): Event? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${Event.TABLE_NAME} WHERE ${Event.COLUMN_ID} = ?", arrayOf(id.toString()))
        Log.d("EventsDatabaseHelper", "Query executed, checking for results.")

        if (cursor.count == 0) {
            Log.d("EventsDatabaseHelper", "No records found for ID: $id")
            return null
        }

        if (cursor.moveToFirst()) {
            Log.d("EventsDatabaseHelper", "Record found, extracting data.")
            val date = cursor.getString(cursor.getColumnIndex(Event.COLUMN_DATE))
            val title = cursor.getString(cursor.getColumnIndex(Event.COLUMN_TITLE))
            val location = cursor.getString(cursor.getColumnIndex(Event.COLUMN_LOCATION))
            val startTime = cursor.getString(cursor.getColumnIndex(Event.COLUMN_START_TIME))
            val endTime = cursor.getString(cursor.getColumnIndex(Event.COLUMN_END_TIME))
            val suggestion = cursor.getString(cursor.getColumnIndex(Event.COLUMN_SUGGESTION))
            val grade = cursor.getInt(cursor.getColumnIndex(Event.COLUMN_GRADE))
            val endNextDay = cursor.getInt(cursor.getColumnIndex(Event.COLUMN_END_NEXT_DAY)) == 1
            Log.d("EventsDatabaseHelper", "Data extracted, creating Event object.")
            return Event(id, date, title, location, startTime, endTime, suggestion, grade, endNextDay)
        } else {
            Log.d("EventsDatabaseHelper", "Cursor couldn't move to the first row.")
            return null
        }
    }

    fun updateEventGrade(eventId: Int, newGrade: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Event.COLUMN_GRADE, newGrade)
        }
        Log.d("EventsDatabaseHelper", "Updating grade for event Id: $eventId to $newGrade")
        db.update(Event.TABLE_NAME, values, "${Event.COLUMN_ID} = ?", arrayOf(eventId.toString()))
        Log.d("EventsDatabaseHelper", "Event grade updated successfully")
    }


    fun getEventsForDataExtractor(): List<Event> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${Event.TABLE_NAME}", null)
        Log.d("EventsDatabaseHelper", "Getting all events")

        val events = mutableListOf<Event>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(Event.COLUMN_ID))
                val date = it.getString(it.getColumnIndex(Event.COLUMN_DATE))
                val title = it.getString(it.getColumnIndex(Event.COLUMN_TITLE))
                val location = it.getString(it.getColumnIndex(Event.COLUMN_LOCATION))
                val startTime = it.getString(it.getColumnIndex(Event.COLUMN_START_TIME))
                val endTime = it.getString(it.getColumnIndex(Event.COLUMN_END_TIME))
                val suggestion = it.getString(it.getColumnIndex(Event.COLUMN_SUGGESTION))
                val grade = it.getInt(it.getColumnIndex(Event.COLUMN_GRADE))
                val endNextDay = it.getInt(it.getColumnIndex(Event.COLUMN_END_NEXT_DAY)) == 1
                events.add(Event(id, date, title, location, startTime, endTime, suggestion, grade, endNextDay))

            }
        }
        Log.d("EventsDatabaseHelper", "Fetched all events for data extraction")
        return events
    }

}
