package com.example.machine_learning_calendar

import android.content.Context

class ModelTrainer(private val context: Context) {

    fun trainModel() {
        val dbHelper = EventsDatabaseHelper(context)
        val dataExtractor = DataExtractor(dbHelper)
        val rawEvents = dataExtractor.extractData()

        val dataCleaner = DataCleaner()
        val cleanedEvents = dataCleaner.cleanData(rawEvents)

        // Here you can add the code to train your model using the cleanedEvents
    }
}
