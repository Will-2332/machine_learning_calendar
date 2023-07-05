package com.example.machine_learning_calendar

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class Event(val id: Int, val date: String, val title: String, val location: String, val startTime: String, val endTime: String, val suggestion: String, val grade: Int) {
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
                "${Event.COLUMN_GRADE} INTEGER)"
        db.execSQL(CREATE_EVENTS_TABLE)
        Log.d("EventsDatabaseHelper", "Creating database : ${Event.TABLE_NAME}")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Event.TABLE_NAME}")
        onCreate(db)
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
                events.add(Event(id, date, title, location, startTime, endTime, suggestion, grade))
            }
        }
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
        }
        Log.d("EventsDatabaseHelper", "Creating event: $event") // Add this line
        db.insert(Event.TABLE_NAME, null, values)
    }
}
