package com.example.machine_learning_calendar


class DataExtractor(private val dbHelper: EventsDatabaseHelper) {
    fun extractData(): List<Event> {
        return dbHelper.getEventsForDataExtractor()
    }
}
